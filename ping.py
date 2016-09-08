#!/usr/bin/python

from __future__ import print_function
try:
	import http.client as httplib
except:
	import httplib
import sys

# SERVER INFORMATION
PING_HOSTNAME = '192.168.1.90'
PING_PORT = 8080

# SESSION COMMANDS
GET_ALL_GAMES = 'getallgames'
GET_GAME = 'getgame'
NEW_GAME = 'newgame'
END_GAME = 'endgame'
GET_STATS = 'stats'

def send_request(method, uri, body=None):
	"""Sends a request to the Ping server"""
	con = httplib.HTTPConnection(PING_HOSTNAME, PING_PORT)
	con.request(method, uri, body)
	res = con.getresponse()
	return res.status, res.reason, res.read()

def get_all_games():
	"""Returns all games"""
	return send_request('GET', '/ping/game')

def get_game(sid):
	"""Returns the game matching the sid"""
	return send_request('GET', '/ping/game/' + sid)

def new_game(sid, players):
	"""Starts a new ping game"""
	body = '{"sid":"' + sid + '","players":["' + '","'.join(players) + '"]}'
	return send_request('POST', '/ping/game/newgame', body)

def end_game(sid):
	"""Ends a game"""
	return send_request('DELETE', '/ping/game/endgame/' + sid)

def stats(sid, uid):
	"""Gets the player stats"""
	return send_request('GET', '/ping/game/' + '/'.join([sid, uid]))

def send_action(sid, uid, action, params=None):
	"""Sends an action"""
	body = '{"action":"' + action + '"'
	if (params != None):
		body = body + ',"params":[' + ','.join(params) + ']'
	body = body + '}'
	return send_request('PUT', '/ping/game/' + '/'.join([sid, uid]), body)

if __name__ == '__main__':
	if len(sys.argv) < 2:
		print('USAGE:', sys.argv[0], '<command> <parameters...>')
		sys.exit(1)

	# Parse the command
	command = sys.argv[1]
	status, reason, res = None, None, None
	if (command == GET_ALL_GAMES):
		status, reason, res = get_all_games()
	elif (command == GET_GAME):
		status, reason, res = get_game(sys.argv[2])
	elif (command == NEW_GAME):
		status, reason, res = new_game(sys.argv[2], sys.argv[3:])
	elif (command == END_GAME):
		status, reason, res = end_game(sys.argv[2])
	elif (command == GET_STATS):
		status, reason, res = stats(sys.argv[2], sys.argv[3])
	else:
		sid = sys.argv[2]
		uid = sys.argv[3]
		params = None
		if (len(sys.argv) > 4):
			params = sys.argv[4:]
		status, reason, res = send_action(sid, uid, command, params)

	# Print the action results
	print(status, reason)
	if (res != ''):
		print(res)
