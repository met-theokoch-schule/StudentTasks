(() => {
  const description = document.getElementById("description");
  const iframe = document.getElementById("h5p-frame");

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

    const matchVerbId = config.matchVerbId;
    if (matchVerbId) {
      const verbId = statement?.verb?.id;
      if (verbId === matchVerbId) {
        console.log("H5P xAPI Treffer", statement);
      }
      return;
    }

    console.log("H5P xAPI Nachricht", statement);
  });
})();
