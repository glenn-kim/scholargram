(function() {
	var app = angular.module("appScholargram", []);
	
	
	// Private Variables
	var _oUser = {
		name: "TEST"
	};
	var _sRootDir = "/~min/scholargram/";
	
	
	// Directive Mapping
	app.directive("sgHeader", function() {
		/*
			NOTE!
			1) replcae: true
				This option works when templateUrl page packaged by a single element.
				Even a single comment wasn't allowed!
			2) scope: true
				This option makes a new local scope for this directive.
				If not true, directive share parent scope.
		*/	
		return {
			restrict: "E",
			templateUrl: "./include/sg-header.html",
			controller: function($scope) {
				$scope.rootDir = _sRootDir;
			},
			scope: true,
			replace: true
		}
	});// D/commonHeader
	
	
	// Controller Declaration
	app.controller("UserController", ["$scope", function($scope) {
		/*
			NOTE!
			1) $scope !== this
			2) "this" keyword will help maintaining controllers' role clearly.
		*/
		this.getUser = function() {
			return _oUser;
		};
		
		this.setUser = function(oUser) {
			_oUser = oUser;
		};
	}]);// C/commonUserCtrl
	
})();

