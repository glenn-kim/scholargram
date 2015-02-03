(function() {
	var appTimeline = angular.module("appTimeline", ["cmnUser", "cmnHeader"]);
	
	// Directive Mapping	
	appTimeline.directive("tlAsideMain", function() {
		return {
			restrict: "E",
			templateUrl: "html/tl-aside-main.html",
			replace: true
		}
	});// D/tlAsideLeft
	
	appTimeline.directive("tlArticle", function() {
		return {
			restrict: "E",
			templateUrl: "html/tl-article.html",
			replace: true,
			controller: function() {
				$("textarea").autosize();
			}
		}
	});// D/tlArticle
	
	appTimeline.directive("tlAsideEtc", function() {
		return {
			restrict: "E",
			templateUrl: "html/tl-aside-etc.html",
			replace: true
		}
	});// D/tlAsideRight
	
	
	
	// Controller Declaration
	appTimeline.controller("MainController", ["userFactory", "headerFactory", function(uf, hf) {
		/*
			NOTE!
			1) $scope !== this
			2) "this" keyword will help maintaining controllers' role clearly.
		*/
		var self = this;
		var _sAsideSelector = ".aside-main .sm.table";
		
		var init = function() {
			uf.getMe().
				success(function(curUser) {
					self.curUser = curUser;
				});
			
			hf.setAsideSelector(_sAsideSelector);
		};
		
		init();
	}]);// C/MainController
	
	appTimeline.controller("TimelineController", ["$http", function($http) {
		var self = this;
		
		self.getTimelineData = function() {
			$http.get("json/timeline").
				success(function(data) {
					self.items = data;
				});
		};
		
		self.getTimelineData();
	}]);
	
	
})();
