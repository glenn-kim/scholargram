(function() {
	var app = angular.module("appScholargram", []);
	
	
	// Directive Mapping
	app.directive("sgHeader", function() {
		/*
			NOTE!
			1) replcae: true
				This option works when templateUrl page packaged by a single element.
				Even a single comment wasn't allowed!
			2) scope: true
				This option makes a new local scope for this directive.
				If not true, directive share parent's $scope.
		*/	
		return {
			restrict: "E",
			templateUrl: "include/sg-header.html",
			replace: true
		}
	});// D/commonHeader
	
	
	// Controller Declaration
	app.controller("UserController", ["$http", "$log", function($http, $log) {
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
	
})();

