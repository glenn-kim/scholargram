"use strict";
(function() {
	var article = angular.module("sgArticle", []);
	
	
	// VARIABLES
	var _apiRoot = "http://180.68.52.138:9000";
	
	var _classes = _apiRoot + "/classes";
	
	var _url = {
		classes: _classes
	};
	
	
	// DIRECTIVES
	article.directive("sgArticleDashboard", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-article-dashboard.html",
			controller: "DashboardController",
			scope: true,
			replace: true
		};
	});
	
	article.directive("sgArticleTimeline", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-article-timeline.html",
			controller: "TimelineController",
			scope: true,
			replace: true
		};
	})
	
	
	// CONTROLLERS
	article.controller("DashboardController", ["$rootScope", "$scope", "$http",
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
	
	article.controller("TimelineController", ["$rootScope", "$scope", "$http", "$location",
	function($rootScope, $scope, $http, $location) {
		var __classId = $location.path();
		var __url = _apiRoot + __classId + "/timeline";

		var __getTimelineItems = function() {
			$http.get(__url).
				success(function(timelineItems) {
					$scope.timelineItems = timelineItems;
				}).
				error(function() {
					$rootScope.$location.path("/");
				});
		};

		var __regExp = new RegExp("/[0-9]+");
		$rootScope.$watch("currentUser", function(newUser) {
			if (newUser) {
				if ($location.path().match(__regExp)) {
					__getTimelineItems();
				}
			}
		});
	}]);

})();
