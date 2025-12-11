ace.define("ace/theme/print_a11y_dark", ["require", "exports", "module", "ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-print-a11y-dark";
exports.cssText = `
.ace-print-a11y-dark .ace_gutter {
  background: #FEFEFE;
  color: #802200;
}

.ace-print-a11y-dark .ace_print-margin {
  width: 1px;
  background: #e0e0e0;
}

.ace-print-a11y-dark {
  background-color: #FEFEFE;
  color: #545454;
  margin-bottom: 15px;
}

.ace-print-a11y-dark .ace_cursor {
  color: #545454;
}

.ace-print-a11y-dark .ace_marker-layer .ace_selection {
  background: rgba(50, 107, 173, 0.15);
}

.ace-print-a11y-dark.ace_multiselect .ace_selection.ace_start {
  box-shadow: 0 0 3px 0px #FEFEFE;
}

.ace-print-a11y-dark .ace_marker-layer .ace_step {
  background: rgb(198, 219, 174);
}

.ace-print-a11y-dark .ace_marker-layer .ace_bracket {
  margin: -1px 0 0 -1px;
  border: 1px solid #545454;
}

.ace-print-a11y-dark .ace_marker-layer .ace_active-line {
  background: rgba(0, 0, 0, 0.05);
}

.ace-print-a11y-dark .ace_gutter-active-line {
  background-color: rgba(0, 0, 0, 0.05);
}

.ace-print-a11y-dark .ace_marker-layer .ace_selected-word {
  border: 1px solid rgba(50, 107, 173, 0.3);
}

.ace-print-a11y-dark .ace_invisible {
  color: #c0c0c0;
}

.ace-print-a11y-dark .ace_keyword,
.ace-print-a11y-dark .ace_meta,
.ace-print-a11y-dark .ace_storage,
.ace-print-a11y-dark .ace_storage.ace_type {
  color: #9400D3;
}

.ace-print-a11y-dark .ace_keyword.ace_operator {
  color: #545454;
}

.ace-print-a11y-dark .ace_constant,
.ace-print-a11y-dark .ace_constant.ace_character,
.ace-print-a11y-dark .ace_constant.ace_other {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_language {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_numeric {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_invalid {
  color: #545454;
  background-color: #D91E18;
}

.ace-print-a11y-dark .ace_invalid.ace_deprecated {
  color: #545454;
  background-color: #A85D00;
}

.ace-print-a11y-dark .ace_support.ace_constant,
.ace-print-a11y-dark .ace_support.ace_function {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_fold {
  background-color: #1F7C93;
  border-color: #545454;
}

.ace-print-a11y-dark .ace_support.ace_class,
.ace-print-a11y-dark .ace_support.ace_type {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_heading,
.ace-print-a11y-dark .ace_markup.ace_heading,
.ace-print-a11y-dark .ace_string {
  color: #008000;
}

.ace-print-a11y-dark .ace_entity.ace_name.ace_tag,
.ace-print-a11y-dark .ace_entity.ace_other.ace_attribute-name,
.ace-print-a11y-dark .ace_meta.ace_tag,
.ace-print-a11y-dark .ace_variable {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_comment {
  color: #802200;
}

.ace-print-a11y-dark .ace_entity.ace_name.ace_function,
.ace-print-a11y-dark .ace_entity.ace_other,
.ace-print-a11y-dark .ace_entity.ace_other.ace_attribute-name,
.ace-print-a11y-dark .ace_variable {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_function {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_parameter {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_indent-guide {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQImWNgYGBgYHB3d/8PAAOIAdULw8qMAAAAAElFTkSuQmCC) right repeat-y;
}

.ace-print-a11y-dark .ace_indent-guide-active {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQI12NgYGBgkDhxev5/AA8HBBb6nXkvAAAAAElFTkSuQmCC) right repeat-y;
}

.ace-print-a11y-dark .ace_paren {
  color: #545454;
}
`;

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass, false);

});

(function() {
  ace.require(["ace/theme/print_a11y_dark"], function(m) {
    if (typeof module == "object" && typeof exports == "object" && module) {
      module.exports = m;
    }
  });
})();
