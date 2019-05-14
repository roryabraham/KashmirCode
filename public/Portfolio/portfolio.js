/*
 File: portfolio.js
 Author: Rory Abraham
 Date: 5/13/19
 */

/**
 * A utility function to replace html opening tags and ampersands with their respective html entities for printing
 *
 * @returns {string} A processed version of the calling string
 */
String.prototype.escapeHtml = function() {
    const tagsToReplace = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;'
    };
    return this.replace(/[&<>]/g, function(tag) {
        return tagsToReplace[tag] || tag;
    });
};

/**
 * A utility function to load a code file from the server,
 * Wrap it in <pre><code> </code></pre> tags,
 * Then insert it in the html container with id provided as parameter
 *
 * @param filepath (string) The filepath of the file on the server
 * @param id (string) The id of the html container (div) to dump the file into
 * @param codeLanguage (string) The language (formatted as language-lang) of the file (for syntax highlighting)
 * @returns {Promise<any>} Resolves by performing desired function when file is loaded.
 *                         Rejects if http request returns error
 */
const readFile = function(filepath, id, codeLanguage) {
    let $div = $("#" + id);
    return new Promise(function(resolve, reject) {
        $.ajax(filepath, {
            type: 'GET',
            dataType: 'text'
        }).done(function (response) {
            response = response.escapeHtml();
            $div.html(response);
            $div.wrapInner("<code class="+codeLanguage+"></code>")
                .wrapInner("<pre></pre>");
            resolve("success");
        }).fail(function(jqXHR, textStatus, errorThrown) {
            reject(Error("http.status" + ": " + textStatus));
        });
    });
};

/**
 * A wrapper function for readFile that highlights the code sample when completed.
 * Parameters are as described in readFile
 *
 * @param filepath
 * @param id
 * @param codeLanguage
 */
const loadCodeSample = function(filepath, id, codeLanguage) {
    readFile(filepath, id, codeLanguage)
        .then(function() {
            Prism.highlightAll(); },
            function(error) {
                alert(error)
            });
};

const loadAllCodeSamples = function(filepaths, language) {
    let codeContainers = document.getElementsByClassName("codeBlock");
    for(let i = 0; i < codeContainers.length; i++) {
        let id = codeContainers[i].id;
        loadCodeSample(filepaths[id], id, language);
    }
};