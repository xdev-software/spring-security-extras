/* Force reload page on Vaadin Post and Websocket Requests when authentication expires */
(function() {
    var origOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function(method, url) {
        if(new URL(document.baseURI).origin === new URL(url, document.baseURI).origin) {
            this.addEventListener('readystatechange', function() {
                if(this.readyState === XMLHttpRequest.HEADERS_RECEIVED 
                    && this.status == 401
                    && this.getResponseHeader('X-Force-Reload') != null) {
                    console.log('Received force reload', location);
                    location.reload();
                }
            });
        }
        origOpen.apply(this, arguments);
    };
    console.debug('Installed XHR adapter');
})();
