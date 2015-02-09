(function() {
	var header = angular.module("sgHeader", ["sgUser"]);
	
	
	// DIRECTIVES
	header.directive("sgHeader", function() {
		return {
			restrict: "E",
			templateUrl: "html/sg-header.html",
			replace: true
		};
	});// D/sgHeader
	
})();