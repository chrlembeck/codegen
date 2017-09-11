var lastSelectedTemplateElement = null;
var lastHighlightedTemplateElement = null;
var CSS_TEMPLATE_FILE_BACKGROUND = "#e8e8e8";
var CSS_TEMPLATE_FILE_HIGHLIGHT_BACKGROUND = "#00fa9a";
var CSS_TEMPLATE_FILE_SELECTION_BACKGROUND = "#ffff00";

function selectToken(templateIdx, tokenIdx) {
    if (lastSelectedTemplateElement !== null) {
    	lastSelectedTemplateElement.style.backgroundColor = CSS_TEMPLATE_FILE_BACKGROUND;
    }
    var id = "token_" + templateIdx + "_" + tokenIdx;
    var element = document.getElementById(id);
    var top = element.offsetTop;
    window.scrollTo(0, top);
    element.style.backgroundColor = CSS_TEMPLATE_FILE_SELECTION_BACKGROUND;
    lastSelectedTemplateElement = element;
}

function highlightToken(templateIdx, tokenIdx) {
    var id = "token_" + templateIdx + "_" + tokenIdx;
    var element = document.getElementById(id);
    var top = element.offsetTop;
    window.scrollTo(0, top);
    element.style.backgroundColor = CSS_TEMPLATE_FILE_HIGHLIGHT_BACKGROUND;
    lastHighlightedTemplateElement = element;
}

function unhighlight() {
    if (lastHighlightedTemplateElement !== null) {
    	if (lastSelectedTemplateElement == lastHighlightedTemplateElement) {
    	    lastHighlightedTemplateElement.style.backgroundColor = CSS_TEMPLATE_FILE_SELECTION_BACKGROUND;
    	} else {
    	    lastHighlightedTemplateElement.style.backgroundColor = CSS_TEMPLATE_FILE_BACKGROUND;
    	}
    }
}