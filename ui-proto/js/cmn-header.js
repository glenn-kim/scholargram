(function() {
	var cmnHeader = angular.module("cmnHeader", []);

	// Private Variables
	var _sAsideSelector;
	var _elAside;
	var _toggleTranslate = function(elTarget, x, y, z) {
		var prefix = getBrowserPrefix();
		var attribute = (prefix == "moz") ? "" : prefix;
		attribute += (attribute) ? "Transform" : "transform";
		
		if (elTarget.style[attribute]) {
			elTarget.style[attribute] = "";
			return;
		}

		value = "translate3d(" + [x, y, z].join() + ")";

		elTarget.style[attribute] = value;
	};
	
	cmnHeader.factory("headerFactory", ["$log", function($log) {
		var headerFactory = {};
		
		headerFactory.setAsideSelector = function(sTarget) {
			$log.log(sTarget);
			_sAsideSelector = sTarget;
		};
		
		return headerFactory;
	}]);
	
	cmnHeader.directive("cmnHeader", function() {
		return {
			restrict: "E",
			templateUrl: "html/cmn-header.html",
			replace: true
		}
	});// D/sgHeader
	
	cmnHeader.controller("HeaderController", ["$scope", "$log", function($scope, $log) {
		$scope.$on("toggleAside", function() {
			_elAside = document.querySelector(_sAsideSelector);
			$log.log(_elAside);
			_toggleTranslate(_elAside, "-260px", 0, 0);
		});
		
		this.toggleAside = function() {
			$scope.$emit("toggleAside");
		};
	}]);
	
	
})();