(function() {
	var app = angular.module("appScholargram", []);
	
	
	// Private Variables;
	var _toggleAside = function() {
		var _elSlidableAside = document.querySelector("aside .sm");

		if (_elSlidableAside.hasClassName("on")) {
			_elSlidableAside.removeClassName("on");
			return;
		}
		
		_elSlidableAside.appendClassName("on");
	};
	
	
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
	app.directive("sgHeader", function() {
		return {
			restrict: "E",
			templateUrl: "include/sg-header.html",
			replace: true,
			controller: function() {
				this.toggleAside = _toggleAside;
			},
			controllerAs: "headerCtrl"
		}
	});// D/sgHeader
	
	app.directive("sgAside", function() {
		return {
			restrict: "E",
			templateUrl: "include/sg-aside.html",
			replace: true
		}
	});// D/sgAside
	
	app.directive("sgArticle", function() {
		return {
			restrict: "E",
			templateUrl: "include/sg-article.html",
			replace: true
		}
	});// D/sgArticle
	
	
	// Controller Declaration
	app.controller("UserController", ["$http", "$scope", "$log", function($http, $scope, $log) {
		/*
			NOTE!
			1) $scope !== this
			2) "this" keyword will help maintaining controllers' role clearly.
		*/
		
		var self = this;
		
		self.curUser;

		// LOGIN
		self.authenticate = function(email, passwd) {
			$http.get("json/user.json").
				success(function(oUser) {
					self.curUser = oUser;
					// TODO: After login
				});
		};
		
		// init
		self.authenticate("min20@nhnnext.org", "1234");
	}]);// C/commonUserCtrl
	
	app.controller("DashboardController", ["$http", "$log", function($http, $log) {
		var self = this;
		
		self.getDashboardData = function() {
			$http.get("json/dashboard.json").
				success(function(data) {
					self.contents = data;
				});
		};
		
		self.getDashboardData();
	}]);
	
})();

