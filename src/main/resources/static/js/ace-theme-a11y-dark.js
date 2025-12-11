ace.define("ace/theme/a11y_dark", ["require", "exports", "module", "ace/lib/dom"], function(require, exports, module) {

exports.isDark = true;
exports.cssClass = "ace-a11y-dark";
exports.cssText = `
.ace-a11y-dark .ace_gutter {
  background: #2b2b2b;
  color: #d4d0ab;
}

.ace-a11y-dark .ace_print-margin {
  width: 1px;
  background: #44475a;
}

.ace-a11y-dark {
  background-color: #2b2b2b;
  color: #f8f8f2;
}

.ace-a11y-dark .ace_cursor {
  color: #f8f8f2;
}

.ace-a11y-dark .ace_marker-layer .ace_selection {
  background: rgba(255, 255, 255, 0.15);
}

.ace-a11y-dark.ace_multiselect .ace_selection.ace_start {
  box-shadow: 0 0 3px 0px #2b2b2b;
}

.ace-a11y-dark .ace_marker-layer .ace_step {
  background: rgb(198, 219, 174);
}

.ace-a11y-dark .ace_marker-layer .ace_bracket {
  margin: -1px 0 0 -1px;
  border: 1px solid #f8f8f2;
}

.ace-a11y-dark .ace_marker-layer .ace_active-line {
  background: rgba(255, 255, 255, 0.1);
}

.ace-a11y-dark .ace_gutter-active-line {
  background-color: rgba(255, 255, 255, 0.1);
}

.ace-a11y-dark .ace_marker-layer .ace_selected-word {
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.ace-a11y-dark .ace_invisible {
  color: #626680;
}

.ace-a11y-dark .ace_keyword,
.ace-a11y-dark .ace_meta,
.ace-a11y-dark .ace_storage,
.ace-a11y-dark .ace_storage.ace_type {
  color: #dcc6e0;
}

.ace-a11y-dark .ace_keyword.ace_operator {
  color: #f8f8f2;
}

.ace-a11y-dark .ace_constant,
.ace-a11y-dark .ace_constant.ace_character,
.ace-a11y-dark .ace_constant.ace_other {
  color: #f5ab35;
}

.ace-a11y-dark .ace_constant.ace_language {
  color: #f5ab35;
}

.ace-a11y-dark .ace_constant.ace_numeric {
  color: #f5ab35;
}

.ace-a11y-dark .ace_invalid {
  color: #f8f8f2;
  background-color: #ff5555;
}

.ace-a11y-dark .ace_invalid.ace_deprecated {
  color: #f8f8f2;
  background-color: #ffb86c;
}

.ace-a11y-dark .ace_support.ace_constant,
.ace-a11y-dark .ace_support.ace_function {
  color: #00e0e0;
}

.ace-a11y-dark .ace_fold {
  background-color: #00e0e0;
  border-color: #f8f8f2;
}

.ace-a11y-dark .ace_support.ace_class,
.ace-a11y-dark .ace_support.ace_type {
  color: #00e0e0;
}

.ace-a11y-dark .ace_heading,
.ace-a11y-dark .ace_markup.ace_heading,
.ace-a11y-dark .ace_string {
  color: #abe338;
}

.ace-a11y-dark .ace_entity.ace_name.ace_tag,
.ace-a11y-dark .ace_entity.ace_other.ace_attribute-name,
.ace-a11y-dark .ace_meta.ace_tag,
.ace-a11y-dark .ace_variable {
  color: #ffa07a;
}

.ace-a11y-dark .ace_comment {
  color: #d4d0ab;
}

.ace-a11y-dark .ace_entity.ace_name.ace_function,
.ace-a11y-dark .ace_entity.ace_other,
.ace-a11y-dark .ace_entity.ace_other.ace_attribute-name,
.ace-a11y-dark .ace_variable {
  color: #ffa07a;
}

.ace-a11y-dark .ace_function {
  color: #00e0e0;
}

.ace-a11y-dark .ace_parameter {
  color: #f5ab35;
}

.ace-a11y-dark .ace_indent-guide {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQImWNgYGBgYHB3d/8PAAOIAdULw8qMAAAAAElFTkSuQmCC) right repeat-y;
}

.ace-a11y-dark .ace_indent-guide-active {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQI12NgYGBgkDhxev5/AA8HBBb6nXkvAAAAAElFTkSuQmCC) right repeat-y;
}

.ace-a11y-dark .ace_paren {
  color: #f8f8f2;
}
`;

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass, false);

});

(function() {
  ace.require(["ace/theme/a11y_dark"], function(m) {
    if (typeof module == "object" && typeof exports == "object" && module) {
      module.exports = m;
    }
  });
})();
