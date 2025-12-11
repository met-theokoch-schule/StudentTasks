// Custom a11y-dark Theme für Ace Editor
// Basierend auf accessibility-optimierten Farben für Farbenblinde
ace.define("ace/theme/a11y-dark", ["require", "exports", "module"], function(require, exports, module) {
    var dom = require("ace/lib/dom");

    var textToMatch = "\
.ace-a11y-dark .ace_gutter {\
  background: #222;\
  color: #999;\
}\
.ace-a11y-dark .ace_print-margin {\
  width: 1px;\
  background: #555;\
}\
.ace-a11y-dark {\
  background-color: #222;\
  color: #f8f8f2;\
}\
.ace-a11y-dark .ace_cursor {\
  color: #f8f8f0;\
}\
.ace-a11y-dark .ace_marker-layer .ace_selection {\
  background: #49483E;\
}\
.ace-a11y-dark.ace_multiselect .ace_selection.ace_start {\
  box-shadow: 0 0 3px 0px #222;\
}\
.ace-a11y-dark .ace_marker-layer .ace_step {\
  background: rgb(102, 82, 0);\
}\
.ace-a11y-dark .ace_marker-layer .ace_bracket {\
  margin: -1px 0 0 -1px;\
  border: 1px solid #555;\
}\
.ace-a11y-dark .ace_marker-layer .ace_active-line {\
  background: #333;\
}\
.ace-a11y-dark .ace_gutter-active-line {\
  background-color: #333;\
}\
.ace-a11y-dark .ace_marker-layer .ace_selected-word {\
  border: 1px solid #49483E;\
}\
.ace-a11y-dark .ace_constant {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_constant.ace_character,\
.ace-a11y-dark .ace_constant.ace_character.ace_escape {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_constant.ace_other {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_constant.ace_numeric {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_entity {\
  color: #f8f8f2;\
}\
.ace-a11y-dark .ace_entity.ace_name.ace_tag {\
  color: #f92672;\
}\
.ace-a11y-dark .ace_entity.ace_other.ace_attribute-name {\
  color: #a6e22e;\
}\
.ace-a11y-dark .ace_entity.ace_other.ace_attribute-name.ace_xml-shadow {\
  color: rgba(166, 226, 46, 0.5);\
}\
.ace-a11y-dark .ace_invalid {\
  color: #f8f8f0;\
  background-color: #f92672;\
}\
.ace-a11y-dark .ace_invalid.ace_deprecated {\
  color: #f8f8f0;\
  background-color: #0db9d7;\
}\
.ace-a11y-dark .ace_support {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_support.ace_constant {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_support.ace_function {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_support.ace_other {\
  color: #0db9d7;\
}\
.ace-a11y-dark .ace_string {\
  color: #e6db74;\
}\
.ace-a11y-dark .ace_string.ace_regexp {\
  color: #e6db74;\
}\
.ace-a11y-dark .ace_variable {\
  color: #f8f8f2;\
}\
.ace-a11y-dark .ace_variable.ace_language {\
  color: #f92672;\
}\
.ace-a11y-dark .ace_variable.ace_parameter {\
  color: #fd971f;\
}\
.ace-a11y-dark .ace_comment {\
  color: #75715e;\
}\
.ace-a11y-dark .ace_keyword {\
  color: #f92672;\
}\
.ace-a11y-dark .ace_keyword.ace_operator {\
  color: #f92672;\
}\
.ace-a11y-dark .ace_indent-guide {\
  background: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYVAAAAEklEQVQImWP4////fwYKACOABAAbBgCB7wAAAABJRU5ErkJggg==\") right repeat-y;\
}\
.ace-a11y-dark .ace_fold {\
  background-color: #a6e22e;\
  border-color: #f8f8f2;\
}\
.ace-a11y-dark .ace_fold:hover {\
  background-color: #a6e22e;\
}\
.ace-a11y-dark .ace_tooltip {\
  background-color: #333;\
  border: 1px solid #666;\
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.8);\
  color: #f8f8f2;\
  padding: 5px 8px;\
}\
";

    dom.importCssString(textToMatch, "ace/theme/a11y-dark");

    var themeModule = require("ace/theme/a11y-dark");
    themeModule.cssClass = "ace-a11y-dark";
    exports.cssClass = "ace-a11y-dark";
});
