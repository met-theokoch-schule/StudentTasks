ace.define("ace/theme/a11y_light", ["require", "exports", "module", "ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-a11y-light";
exports.cssText = `
.ace-a11y-light .ace_gutter {
  background: #fefefe;
  color: #802200;
}

.ace-a11y-light .ace_print-margin {
  width: 1px;
  background: #e0e0e0;
}

.ace-a11y-light {
  background-color: #fefefe;
  color: #545454;
  margin-bottom: 15px;
}

.ace-a11y-light .ace_cursor {
  color: #545454;
}

.ace-a11y-light .ace_marker-layer .ace_selection {
  background: rgba(0, 0, 0, 0.1);
}

.ace-a11y-light.ace_multiselect .ace_selection.ace_start {
  box-shadow: 0 0 3px 0px #fefefe;
}

.ace-a11y-light .ace_marker-layer .ace_step {
  background: rgb(200, 220, 170);
}

.ace-a11y-light .ace_marker-layer .ace_bracket {
  margin: -1px 0 0 -1px;
  border: 1px solid #545454;
}

.ace-a11y-light .ace_marker-layer .ace_active-line {
  background: rgba(0, 0, 0, 0.07);
}

.ace-a11y-light .ace_gutter-active-line {
  background-color: rgba(0, 0, 0, 0.07);
}

.ace-a11y-light .ace_marker-layer .ace_selected-word {
  border: 1px solid rgba(0, 0, 0, 0.2);
}

.ace-a11y-light .ace_invisible {
  color: #999;
}

.ace-a11y-light .ace_keyword,
.ace-a11y-light .ace_meta,
.ace-a11y-light .ace_storage,
.ace-a11y-light .ace_storage.ace_type {
  color: #9400d3;
}

.ace-a11y-light .ace_keyword.ace_operator {
  color: #545454;
}

.ace-a11y-light .ace_constant,
.ace-a11y-light .ace_constant.ace_character,
.ace-a11y-light .ace_constant.ace_other {
  color: #a85d00;
}

.ace-a11y-light .ace_constant.ace_language {
  color: #a85d00;
}

.ace-a11y-light .ace_constant.ace_numeric {
  color: #a85d00;
}

.ace-a11y-light .ace_invalid {
  color: #545454;
  background-color: #d91e18;
}

.ace-a11y-light .ace_invalid.ace_deprecated {
  color: #545454;
  background-color: #d91e18;
}

.ace-a11y-light .ace_support.ace_constant,
.ace-a11y-light .ace_support.ace_function {
  color: #1f7c93;
}

.ace-a11y-light .ace_fold {
  background-color: #1f7c93;
  border-color: #545454;
}

.ace-a11y-light .ace_support.ace_class,
.ace-a11y-light .ace_support.ace_type {
  color: #1f7c93;
}

.ace-a11y-light .ace_heading,
.ace-a11y-light .ace_markup.ace_heading,
.ace-a11y-light .ace_string {
  color: #008000;
}

.ace-a11y-light .ace_entity.ace_name.ace_tag,
.ace-a11y-light .ace_entity.ace_other.ace_attribute-name,
.ace-a11y-light .ace_meta.ace_tag,
.ace-a11y-light .ace_variable {
  color: #d91e18;
}

.ace-a11y-light .ace_comment {
  color: #802200;
}

.ace-a11y-light .ace_entity.ace_name.ace_function,
.ace-a11y-light .ace_entity.ace_other,
.ace-a11y-light .ace_entity.ace_other.ace_attribute-name,
.ace-a11y-light .ace_variable {
  color: #d91e18;
}

.ace-a11y-light .ace_function {
  color: #1f7c93;
}

.ace-a11y-light .ace_parameter {
  color: #a85d00;
}

.ace-a11y-light .ace_indent-guide {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQImWPg4ODwn5GBgQEAAgEAUK+IkH6oC2QAAAAASUVORK5CYII=) right repeat-y;
}

.ace-a11y-light .ace_indent-guide-active {
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQI12PgYPj/n4GBgQEAAgEAUK+IkH6oC2QAAAAASUVORK5CYII=) right repeat-y;
}

.ace-a11y-light .ace_paren {
  color: #545454;
}
`;

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass, false);

});

(function() {
  ace.require(["ace/theme/a11y_light"], function(m) {
    if (typeof module == "object" && typeof exports == "object" && module) {
      module.exports = m;
    }
  });
})();
