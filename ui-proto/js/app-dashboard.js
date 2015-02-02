(function() {
	var appDashboard = angular.module("appDashboard", ["cmnUser", "cmnHeader"]);
		
	/*
		NOTE!
		1) replcae: true
			This option works when templateUrl page packaged by a single element.
			Even a single comment wasn't allowed!
		2) scope: true
			This option makes a new local scope for this directive.
			If not true, directive share parent's $scope.
	*/
	
	// Directive Mapping	
	appDashboard.directive("dbAside", function() {
		return {
			restrict: "E",
			templateUrl: "html/db-aside.html",
			replace: true
		}
	});// D/dbAside
	
	appDashboard.directive("dbArticle", function() {
		return {
			restrict: "E",
			templateUrl: "html/db-article.html",
			replace: true
		}
	});// D/dbArticle
	
	
	// Controller Declaration
	appDashboard.controller("MainController", ["userFactory", "headerFactory", function(uf, hf) {
		/*
			NOTE!
			1) $scope !== this
			2) "this" keyword will help maintaining controllers' role clearly.
		*/
		var self = this;
		var _sAsideSelector = "aside .sm.table"
		
		var getUser = function() {
			uf.getMe().
				success(function(curUser) {
					self.curUser = curUser;
				});
				
			hf.setAsideSelector(_sAsideSelector);
		};
		
		getUser();
	}]);// C/MainController
	
	appDashboard.controller("DashboardController", ["$http", "$log", function($http, $log) {
		var self = this;
		
		self.getDashboardData = function() {
			$http.get("json/dashboard").
				success(function(data) {
					self.classTypes = data;
				});
		};
		
		self.getDashboardData();
	}]);
	
})();

