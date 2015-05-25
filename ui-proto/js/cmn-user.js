(function() {
	var cmnUser = angular.module("cmnUser", []);
	
	cmnUser.factory("userFactory", ["$http","$log", function($http, $log) {
		var userFactory = {};
		
		userFactory.getMe = function() {
			return $http.get("json/me");
		};
		
		return userFactory;
	}]);
	
})();