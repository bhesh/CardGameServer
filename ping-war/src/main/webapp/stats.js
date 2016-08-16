function createPlayersTable(data) {
	var playerstable = document.createElement('table');
	var playerstbody = document.createElement('tbody');
	playerstable.appendChild(playerstbody);
	var headers = document.createElement('tr');
	playerstbody.appendChild(headers);
	var huid = document.createElement('th');
	headers.appendChild(huid);
	huid.appendChild(document.createTextNode('UID'));
	var hname = document.createElement('th');
	headers.appendChild(hname);
	hname.appendChild(document.createTextNode('Name'));
	var hscore = document.createElement('th');
	headers.appendChild(hscore);
	hscore.appendChild(document.createTextNode('Score'));
	var hstate = document.createElement('th');
	headers.appendChild(hstate);
	hstate.appendChild(document.createTextNode('State'));
	$.each(data, function(i, item) {
		var player = document.createElement('tr');
		playerstbody.appendChild(player);
		var uid = document.createElement('td');
		player.appendChild(uid);
		uid.appendChild(document.createTextNode(item.uid));
		var name = document.createElement('td');
		player.appendChild(name);
		name.appendChild(document.createTextNode(item.name));
		var score = document.createElement('td');
		player.appendChild(score);
		score.appendChild(document.createTextNode(item.score));
		var state = document.createElement('td');
		player.appendChild(state);
		state.appendChild(document.createTextNode(item.state));
	});
	return playerstable;
}

function populateTable() {
	$.getJSON( 'game', function(data) {
		// Record number of games
		document.getElementById('#numgames').appendChild(
			document.createTextNode('Number of games: '.concat(data.numgames))
		);

		// Populate table
		if (data.numgames > 0) {
			var table = document.getElementById('#stats');
			var tbody = document.createElement('tbody');
			table.appendChild(tbody);
			$.each(data.games, function(i, item) {
				// SID
				var sid = document.createElement('tr');
				tbody.appendChild(sid);
				var sidkey = document.createElement('th');
				sid.appendChild(sidkey);
				sidkey.appendChild(document.createTextNode('SID'));
				var sidval = document.createElement('th');
				sid.appendChild(sidval);
				sidval.appendChild(document.createTextNode(item.sid));

				// Round
				var round = document.createElement('tr');
				tbody.appendChild(round);
				var roundkey = document.createElement('td');
				round.appendChild(roundkey);
				roundkey.appendChild(document.createTextNode('Round'));
				var roundval = document.createElement('td');
				round.appendChild(roundval);
				roundval.appendChild(document.createTextNode(item.round));

				// Deck Size
				var decksize = document.createElement('tr');
				tbody.appendChild(decksize);
				var decksizekey = document.createElement('td');
				decksize.appendChild(decksizekey);
				decksizekey.appendChild(document.createTextNode('Deck Size'));
				var decksizeval = document.createElement('td');
				decksize.appendChild(decksizeval);
				decksizeval.appendChild(document.createTextNode(item.decksize));

				// Discard
				var discard = document.createElement('tr');
				tbody.appendChild(discard);
				var discardkey = document.createElement('td');
				discard.appendChild(discardkey);
				discardkey.appendChild(document.createTextNode('Discard'));
				var discardval = document.createElement('td');
				discard.appendChild(discardval);
				discardval.appendChild(document.createTextNode(item.discard));

				// Turn
				var turn = document.createElement('tr');
				tbody.appendChild(turn);
				var turnkey = document.createElement('td');
				turn.appendChild(turnkey);
				turnkey.appendChild(document.createTextNode('Turn'));
				var turnval = document.createElement('td');
				turn.appendChild(turnval);
				turnval.appendChild(document.createTextNode(item.players[item.turn].uid));

				// Players
				var players = document.createElement('tr');
				tbody.appendChild(players);
				var playerskey = document.createElement('td');
				players.appendChild(playerskey);
				playerskey.appendChild(document.createTextNode('Players'));
				var playersval = document.createElement('td');
				players.appendChild(playersval);
				playersval.appendChild(createPlayersTable(item.players));
			});
		}
	});
}
populateTable();
