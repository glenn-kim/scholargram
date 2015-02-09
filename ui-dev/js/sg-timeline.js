"use strict";
(function() {
	var timeline = angular.module("sgTimeline", []);
	
	
	// VARIABLES
	var _apiRoot = "http://180.68.52.138:9000";
	
	
	// DIRECTIVES
	timeline.directive("sgTimeline", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-timeline.html",
			controller: "TimelineController",
			scope: true,
			replace: true
		};
	})
	
	
	// CONTROLLERS
	timeline.controller("TimelineController", ["$rootScope", "$scope", "$http", "$location",
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
