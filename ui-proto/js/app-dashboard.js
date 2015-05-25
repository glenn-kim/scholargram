(function() {
	var appDashboard = angular.module("appDashboard", ["cmnUser", "cmnHeader", "customFilters"]);
		
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
		
		var init = function() {
			uf.getMe().
				success(function(curUser) {
					self.curUser = curUser;
				});
				
			hf.setAsideSelector(_sAsideSelector);
		};
		
		init();
	}]);// C/MainController
	
	appDashboard.controller("DashboardController", ["$http", function($http, $log) {
		var self = this;
		var curDate = new Date();
		
		self.classes = [
			{ ongoing: [] },
			{ planned: [] },
			{ finished: [] }
		];
		
		self.getDashboardData = function() {
			$http.get("json/classes.json").
				success(function(classes) {
					for (var idx in classes) {
						var klass = classes[idx];

						if (klass.startDate <= curDate ) {
							if (curDate <= klass.endDate) {
								self.classes[0].ongoing.push(klass);
							} else {
								self.classes[2].finished.push(klass);
							}
						} else {
							self.classes[1].planned.push(klass);
						}
					}
				});
		};
		
		self.getDashboardData();
	}]);
	
})();

