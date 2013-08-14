function writeFSMessage(message) {
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";
	pre.innerHTML = message;
	
	document.getElementById("fsoutput").appendChild(pre);
}

function createFSWebSocket() {
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
	fsWebSocket = new WS("ws://localhost:9000/monitor");
	
	fsWebSocket.onopen = function() {
		writeFSMessage("WebSocket connected.");
	};
	fsWebSocket.onclose = function() {
		writeFSMessage("WebSocket disconnected.");
	};
	fsWebSocket.onerror = function(event) {
		writeFSMessage('<span style="color: red;">ERROR: ' + event.data	+ '</span>');
		fsWebSocket.close();
	};
	fsWebSocket.onmessage = function(event) {
		writeFSMessage('<span style="color: blue;">RECEIVED: ' + event.data	+ '</span>');
	};
}

window.onload = createFSWebSocket