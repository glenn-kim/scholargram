(function() {
	var app = angular.module("scholargram", ["sgHeader", "sgArticle"]);
	
	app.config(["$locationProvider", function($locationProvider) {
		//$locationProvider.html5Mode(true);		
	}]);
	
	// CONTROLLERS
	app.controller("MainController", ["$rootScope", "$http", "$location", "getMe",
	function($rootScope, $http, $location, getMe) {
		/*
			1) init 하면서 getMe() 해봄.
			2-1) getMe()가 있음 -> 로그인 되어있었다 -> URL 바꾸지 않고 그냥 통과.
				root url은 dashboard, {class id}/ 이면 해당 timeline
			2-2) getMe()가 없음 -> root url로 바꿈.
				
		*/
		
		var __init = function() {
			$rootScope.currentUser = getMe();
			$rootScope.$location.path($location.path());
		};

		$rootScope.$location = $location;

		$rootScope.currentUser;
		$rootScope.setCurrentUser = function(newUser) {
			$rootScope.currentUser = newUser;
		};
		
		__init();
	}]);
})();
