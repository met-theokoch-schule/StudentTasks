ace.define("ace/theme/print_a11y_dark", ["require", "exports", "module", "ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-print-a11y-dark";
exports.cssText = `
.ace-print-a11y-dark .ace_gutter {
  background: #FEFEFE;
  color: #545454;
}

.ace-print-a11y-dark .ace_print-margin {
  width: 1px;
  background: #e0e0e0;
}

.ace-print-a11y-dark {
  background-color: #FEFEFE;
  color: #545454;
}

.ace-print-a11y-dark .ace_cursor {
  color: #545454;
}

.ace-print-a11y-dark .ace_marker-layer .ace_selection {
  background: rgba(50, 107, 173, 0.2);
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

.ace-print-a11y-dark .ace_new-constant {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_character,
.ace-print-a11y-dark .ace_constant.ace_character.ace_escape {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_other {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_support,
.ace-print-a11y-dark .ace_support.ace_constant,
.ace-print-a11y-dark .ace_support.ace_type,
.ace-print-a11y-dark .ace_support.ace_class,
.ace-print-a11y-dark .ace_support.ace_function {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_support.ace_other {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_string {
  color: #008000;
}

.ace-print-a11y-dark .ace_string.ace_regexp {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_string.ace_escape {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_string.ace_unquoted {
  color: #008000;
}

.ace-print-a11y-dark .ace_punctuation {
  color: #545454;
}

.ace-print-a11y-dark .ace_punctuation.ace_tag {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_support.ace_tag-bracket {
  color: #545454;
}

.ace-print-a11y-dark .ace_meta.ace_selector {
  color: #9400D3;
}

.ace-print-a11y-dark .ace_meta.ace_tag {
  color: #545454;
}

.ace-print-a11y-dark .ace_invalid {
  color: #FEFEFE;
  background-color: #D91E18;
}

.ace-print-a11y-dark .ace_invalid.ace_deprecated {
  color: #FEFEFE;
  background-color: #A85D00;
}

.ace-print-a11y-dark .ace_fold {
  background-color: #326BAD;
  border-color: #545454;
}

.ace-print-a11y-dark .ace_entity.ace_name.ace_function {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_entity.ace_name.ace_tag {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_entity.ace_other.ace_attribute-name {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_entity.ace_other.ace_attribute-name.ace_html {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_entity.ace_other.ace_attribute-name.ace_html.ace_boolean {
  color: #9400D3;
}

.ace-print-a11y-dark .ace_entity.ace_name {
  color: #545454;
}

.ace-print-a11y-dark .ace_comment {
  color: #802200;
}

.ace-print-a11y-dark .ace_comment.ace_doc {
  color: #802200;
}

.ace-print-a11y-dark .ace_comment.ace_doc.ace_tag {
  color: #802200;
}

.ace-print-a11y-dark .ace_comment.ace_cdata {
  color: #802200;
}

.ace-print-a11y-dark .ace_indent-guide {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='8'%3E%3Ccircle cx='4' cy='4' r='1.5' fill='%23d0d0d0'/%3E%3C/svg%3E") right repeat-y;
}

.ace-print-a11y-dark .ace_indent-guide-active {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='8'%3E%3Ccircle cx='4' cy='4' r='1.5' fill='%23a0a0a0'/%3E%3C/svg%3E") right repeat-y;
}

.ace-print-a11y-dark .ace_variable {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_variable.ace_parameter {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_heading,
.ace-print-a11y-dark .ace_markup.ace_heading {
  color: #D91E18;
  background-color: transparent;
  font-weight: bold;
}

.ace-print-a11y-dark .ace_heading.ace_1,
.ace-print-a11y-dark .ace_heading.ace_2,
.ace-print-a11y-dark .ace_heading.ace_3,
.ace-print-a11y-dark .ace_heading.ace_4,
.ace-print-a11y-dark .ace_heading.ace_5,
.ace-print-a11y-dark .ace_heading.ace_6 {
  background-color: transparent;
}

.ace-print-a11y-dark .ace_list {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_markup.ace_list {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_blockquote {
  color: #802200;
}

.ace-print-a11y-dark .ace_blockquote.ace_1 {
  color: #008000;
}

.ace-print-a11y-dark .ace_blockquote.ace_2 {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_blockquote.ace_3 {
  color: #9400D3;
}

.ace-print-a11y-dark .ace_blockquote.ace_4 {
  color: #326BAD;
}

.ace-print-a11y-dark .ace_blockquote.ace_5 {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_blockquote.ace_6 {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_emphasis {
  color: #545454;
  font-style: italic;
}

.ace-print-a11y-dark .ace_emphasis.ace_1 {
  color: #545454;
  font-style: italic;
}

.ace-print-a11y-dark .ace_emphasis.ace_2 {
  color: #545454;
  font-weight: bold;
}

.ace-print-a11y-dark .ace_emphasis.ace_3 {
  color: #545454;
  font-weight: bold;
  font-style: italic;
}

.ace-print-a11y-dark .ace_markup.ace_underline {
  text-decoration: underline;
}

.ace-print-a11y-dark .ace_markup.ace_underline.ace_link {
  color: #326BAD;
}

.ace-print-a11y-dark .ace_link {
  color: #326BAD;
}

.ace-print-a11y-dark .ace_punctuation.ace_link {
  color: #326BAD;
}

.ace-print-a11y-dark .ace_list.ace_1 {
  color: #D91E18;
}

.ace-print-a11y-dark .ace_language.ace_literal {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_language.ace_boolean {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_language.ace_null {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_language.ace_undefined {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_buildin {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_constant.ace_language.ace_boolean {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_constant.ace_language.ace_nan {
  color: #A85D00;
}

.ace-print-a11y-dark .ace_invalid.ace_illegal {
  color: #FEFEFE;
  background-color: #D91E18;
}

.ace-print-a11y-dark .ace_invalid.ace_legal {
  color: #FEFEFE;
  background-color: #802200;
}

.ace-print-a11y-dark .ace_support.ace_magic {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_line-bookmark {
  background-color: #ffc40d;
  border-radius: 50%;
}

.ace-print-a11y-dark .ace_error-anchor {
  background-color: #D91E18;
}

.ace-print-a11y-dark .ace_type {
  color: #1F7C93;
}

.ace-print-a11y-dark .ace_collab.ace_user1 {
  color: #FEFEFE;
  background-color: #326BAD;
}

.ace-print-a11y-dark .ace_collab.ace_user2 {
  color: #FEFEFE;
  background-color: #9400D3;
}

.ace-print-a11y-dark .ace_collab.ace_user3 {
  color: #FEFEFE;
  background-color: #A85D00;
}

.ace-print-a11y-dark .ace_collab.ace_user4 {
  color: #FEFEFE;
  background-color: #1F7C93;
}

.ace-print-a11y-dark .ace_diff.ace_addition {
  background-color: transparent;
  color: #008000;
}

.ace-print-a11y-dark .ace_diff.ace_deletion {
  background-color: transparent;
  color: #D91E18;
}

.ace-print-a11y-dark .ace_diff.ace_header {
  background-color: #326BAD;
  color: #FEFEFE;
}
`;

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
