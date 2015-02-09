"use strict";
(function() {
	var dashboard = angular.module("sgDashboard", []);
	
	
	// VARIABLES
	var _apiRoot = "http://180.68.52.138:9000";
	
	var _classes = _apiRoot + "/classes";
	
	var _url = {
		classes: _classes
	};
	
	
	// DIRECTIVES
	dashboard.directive("sgDashboardAside", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-dashboard-aside.html",
			replace: true
		};
	});
	
	dashboard.directive("sgDashboardArticle", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-dashboard-article.html",
			controller: "DashboardController",
			scope: true,
			replace: true
		};
	});
	
	
	
	// CONTROLLERS
	dashboard.controller("DashboardController", ["$rootScope", "$scope", "$http",
	function($rootScope, $scope, $http) {
		var __getClasses = function() {
			$http.get(_url.classes).
				success(function(classes) {
					$scope.classes = classes;
				}).
				error(function() {
					$rootScope.$location.path("/");
				})
		};
		
		$rootScope.$watch("currentUser", function(newUser) {
			if (newUser) {
				__getClasses();
			}
		});
	}]);
})();
