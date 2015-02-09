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
		
		// TEST
		// 학생으로 로그인 되어있을 시 FORBIDDEN
		$http.get(_apiRoot + __classId + "/registrations").
			success(function(data) {
				console.log(data);
			});
		
		// item 정보. 이거 써먹을 수 있도록 페이지 만들어야.
		$http.get(_apiRoot + __classId + "/1").
			success(function(data) {
				console.log(data);
			});
		
		// assignment 정보
		$http.get(_apiRoot + __classId + "/2/submissions").
			success(function(data) {
				console.log(data);
			});
			
		// class 정보
		$http.get(_apiRoot + __classId).
			success(function(data) {
				console.log(data);
			});
		// /TEST
		
		var __regExp = new RegExp("/[0-9]+");
		$rootScope.$watch("currentUser", function(newUser) {
			if (newUser) {
				if ($location.path().match(__regExp)) {
					__getTimelineItems();
				}
			}
		});
		
		$scope.prof = {};
		$scope.prof.activeTab = "alert";
		$scope.prof.isSelected = function(tabName) {
			return $scope.prof.activeTab == tabName;
		};
		$scope.prof.activate = function(tabName) {
			$scope.prof.activeTab = tabName;
		};
		
		$scope.writeAlert = function(text) {
			$http.post(_apiRoot + __classId + "/timeline", {
				itemType: "alert",
				data: {
					text: text
				}
			}).success(function() {
				__getTimelineItems()
			});
		}
		
		$scope.writeLecture = function(title) {
			$http.post(_apiRoot + __classId + "/timeline", {
				itemType: "lecture",
				data: {
					title: title,
					attachments: []
				}
			}).success(function() {
				__getTimelineItems()
			});
		}

		$scope.writeAssignment = function(title, description, dueDate) {
			var longDueDate = new Date(dueDate).getTime();
			console.log({
				itemType: "assignment",
				data: {
					title: title,
					description: description,
					due_datetime: longDueDate,
					attachments: []
				}
			});

			$http.post(_apiRoot + __classId + "/timeline", {
				itemType: "assignment",
				data: {
					title: title,
					description: description,
					due_datetime: longDueDate,
					attachments: []
				}
			}).success(function() {
				__getTimelineItems()
			});
		}
				
		// autosize on textarea
		$("textarea").autosize();
	}]);
})();
