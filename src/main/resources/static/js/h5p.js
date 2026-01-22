(() => {
  const description = document.getElementById("description");
  const iframe = document.getElementById("h5p-frame");
  const saveUrl = document.getElementById("task-save-url")?.dataset.url || "";
  const submitUrl =
    document.getElementById("task-submit-url")?.dataset.url || "";
  let isSubmitting = false;
  let hasSubmitted = false;

  let config = {};
  if (description && description.textContent) {
    try {
      config = JSON.parse(description.textContent);
    } catch (error) {
      console.error("H5P config JSON konnte nicht geparst werden.", error);
    }
  }

  if (!config.url) {
    console.error("H5P config fehlt: url");
  } else if (iframe) {
    iframe.src = config.url;
  }

  const allowedOrigins = Array.isArray(config.allowedOrigins)
    ? config.allowedOrigins
    : null;

  function extractScore(statement) {
    const score = statement?.result?.score;
    if (!score) return null;
    if (typeof score.scaled === "number") {
      return { value: score.scaled, source: "scaled" };
    }
    if (typeof score.raw === "number") {
      return { value: score.raw, source: "raw" };
    }
    return null;
  }

  function buildContent(scoreInfo) {
    return JSON.stringify({
      score: scoreInfo.value,
      scoreSource: scoreInfo.source,
      submittedAt: new Date().toISOString(),
    });
  }

  async function submitScore(scoreInfo) {
    if (hasSubmitted || isSubmitting) return;
    if (!submitUrl) {
      if (!saveUrl) {
        console.warn("Keine URL fuer Speicherung/Abgabe gefunden");
      }
      return;
    }

    isSubmitting = true;
    try {
      await fetch(submitUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content: buildContent(scoreInfo) }),
      });
      hasSubmitted = true;
    } catch (error) {
      console.error("Fehler beim Abgeben des H5P Scores:", error);
    } finally {
      isSubmitting = false;
    }
  }

  window.addEventListener("message", (event) => {
    if (allowedOrigins && !allowedOrigins.includes(event.origin)) {
      return;
    }

    const payload = event.data;
    if (!payload || payload.type !== "h5p-xapi") {
      return;
    }

    const statement = payload.statement || payload;
    if (!statement) {
      return;
    }
    const hasResult = Boolean(statement.result);

    const matchVerbId = config.matchVerbId;
    if (matchVerbId) {
      const verbId = statement?.verb?.id;
      if (verbId === matchVerbId) {
        const scoreInfo = extractScore(statement);
        if (hasResult) {
          console.log("H5P message (raw)", {
            origin: event.origin,
            data: event.data,
          });
          console.log("H5P xAPI Treffer", statement);
          const passScoreScaled = config.passScoreScaled;
          if (typeof passScoreScaled === "number") {
            if (scoreInfo && scoreInfo.source === "scaled") {
              const passed = scoreInfo.value >= passScoreScaled;
              console.log("H5P xAPI Bewertung", {
                scaledScore: scoreInfo.value,
                passScoreScaled,
                passed,
              });
              if (passed) {
                submitScore(scoreInfo);
              }
            } else {
              console.log("H5P xAPI Bewertung fehlt: result.score.scaled");
            }
          } else {
            console.log("H5P xAPI Bewertung uebersprungen", {
              reason: "passScoreScaled fehlt",
              matchVerbId: config.matchVerbId || null,
              origin: event.origin,
            });
          }
        }
      }
      return;
    }

    if (hasResult) {
      console.log("H5P message (raw)", {
        origin: event.origin,
        data: event.data,
      });
      console.log("H5P xAPI Nachricht", statement);
    }
  });
})();
