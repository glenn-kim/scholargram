"use strict";
(function() {
	var user = angular.module("sgUser", []);
	
	
	// VARIABLES
	var _apiRoot = "http://180.68.52.138:9000";
	
	var _me = _apiRoot + "/me";
	var _login = _apiRoot + "/login";
	var _logout = _apiRoot + "/logout";

	var _url = {
		me: _me,
		login: _login,
		logout: _logout
	};
	
	
	// FACTORIES	
	user.factory("getMe", function() {
		var reseult;
		var http = new XMLHttpRequest();

		http.open("GET", _url.me, false);
		http.send();
				
		return function() {
			if (http.status !== 200) {
				return;
			}
			return JSON.parse(http.responseText);
		};
	});
	
	
	// CONTROLLERS
	// USE ONLY SYNCHRONOUS XMLHttpRequest WHEN USER LOGIN / LOGOUT
	user.controller("UserController", ["$rootScope", "$scope", "$http",
	function($rootScope, $scope, $http) {
		// variables
		var __login = function(email, password) {
			var user = {
				"email": email,
				"password": password
			};

			$http.post(_url.login, user).
				success(function(currentUser) {
					$rootScope.setCurrentUser(currentUser);
				}).
				error(function() {
					console.error("login: NO SUCH USER");
				});
		};
		
		var __logout = function() {
			$http.post(_url.logout).
				success(function() {
					$rootScope.setCurrentUser(undefined);
					// logout 시 root url로 변경
					$rootScope.$location.path("/");
				});
		};
		
		// access $scope or self(this)
		$scope.login = __login;
		$scope.logout = __logout;
	}]);
	
})();
