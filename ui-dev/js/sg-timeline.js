"use strict";
(function() {
	var timeline = angular.module("sgTimeline", []);
	
	
	// VARIABLES
	var _apiRoot = "http://180.68.52.138:9000";
	
	
	// DIRECTIVES
	timeline.directive("sgTimelineAside", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-timeline-aside.html",
			replace: true
		};
	});
	
	timeline.directive("sgTimelineArticle", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-timeline-article.html",
			controller: "TimelineController",
			scope: true,
			replace: true,
		};
	});
	
	
	// CONTROLLERS
	timeline.controller("TimelineController", ["$rootScope", "$scope", "$http", "$location", "$log",
	function($rootScope, $scope, $http, $location, $log) {
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
		
				
		$http.get(_apiRoot + __classId + "/registrations").
			success(function(data) {
				console.log(data);
			});

		var __regExp = new RegExp("/[0-9]+");
		$rootScope.$watch("currentUser", function(newUser) {
			if (newUser) {
				if ($location.path().match(__regExp)) {
					__getTimelineItems();
				}
			}
		});

		// autosize on textarea
		$("textarea").autosize();
	}]);
})();
