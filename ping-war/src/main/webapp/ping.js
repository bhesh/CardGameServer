// Canvas
var canvas = document.getElementById("gameboard");
var context = canvas.getContext("2d");

// Colors
var canvasColor = "#ffffff";
var menuColor = "#0099bb";
var menuTextColor = "#ffffff";
var menuTextFont = "20px monospace";
var turnMarkerColor = "#660088";

// Deck
var deckSize = 53;
var cardWidth = 72;
var cardHeight = 96;
var images = new Array();

// Players
function Player(uid, name) {
	this.uid = uid;
	this.name = name;
	this.score = 0;
}

// Game state
function Game(sid, names) {
	this.sid = sid;
	this.players = new Array();
	this.turn = 0;
	for (i in names) {
		this.players[i] = new Player(names[i]);
	}
}

/**
 * Loads the cards into the cache (I believe)
 */
function loadCards() {
	images[0] = new Image();
	images[0].src = "/classic-cards/b1fv.jpg";
	console.log("loaded ".concat(images[0].src));
	var i = 1;
	for (i=1; i <=53; ++i) {
		images[i] = new Image();
		images[i].src = "/classic-cards/".concat(
			i.toString().concat(".jpg")
		);
		console.log("loaded ".concat(images[i].src));
	}
}

/**
 * Draws the board
 */
function drawCanvas() {
	context.fillStyle = canvasColor;
	context.fillRect(0, 0, canvas.width, canvas.height);
}

/**
 * Gets the game state
 */
function getGameState(id) {
	$.getJSON( "game/".concat(id), function(data) {
		player = data.uid
		
	})
	.done(function() {
		console.log("Successfully fetched game stats for id ".concat(id));
	})
	.fail(function() {
		console.log("Failed fetching game stats for id ".concat(id));
	})
}

/**
 * Draws the current player's hand
 */


/**
 * Draws the game
 */
function drawGame() {
}

/**
 * Initialize everything
 */
var game = new Game("test", ["alexa", "brian", "shawna"]);
console.log("initializing");
loadCards();
console.log("drawing canvas");
drawCanvas();
console.log("drawing game");
drawGame();

