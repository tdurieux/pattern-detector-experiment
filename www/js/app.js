angular.module('defects4j-website', ['ngRoute', 'ui.bootstrap', 'anguFixedHeaderTable'])
	.config(function($routeProvider, $locationProvider) {
		$routeProvider
			.when('/bug/:benchmark/:id', {
				controller: 'bugController'
			})
			.when('/', {
				controller: 'mainController'
			});
		$locationProvider.html5Mode(false);
	})
	.directive('keypressEvents', [
		'$document',
		'$rootScope',
		function($document, $rootScope) {
			return {
				restrict: 'A',
				link: function() {
					$document.bind('keydown', function(e) {
						$rootScope.$broadcast('keypress', e);
						$rootScope.$broadcast('keypress:' + e.which, e);
					});
				}
			};
		}
	]).directive('diff', ['$http', function ($http) {
		return {
			restrict: 'A',
			scope: {
				patch: '=diff'
			},
			link: function (scope, elem, attrs) {
				function prepareDiff(diff) {
					if (diff != null && diff != '') {
						var regex_origin = /--- ([^ ]+).*/.exec(diff)
						if (regex_origin) { 
							origin = regex_origin[1]
							dest = /\+\+\+ ([^ ]+).*/.exec(diff)[1]
							if (dest.indexOf(origin) > 0) {
								diff = diff.replace(dest, origin)
							}
							diff = diff.replace(/\\"/g, '"').replace(/\\n/g, "\n").replace(/\\t/g, "\t")
						}
						var diff2htmlUi = new Diff2HtmlUI({ diff: diff });
						diff2htmlUi.draw($(elem), {showFiles: false, matching: 'none'});
						diff2htmlUi.highlightCode($(elem));
					}
				}
				function printDiff(patch) {
					$(elem).text('')
					if (patch.metrics.patchSize > 10000) {
						return $(elem).text('The diff is too big to be displayed')
					}
					var diff = patch.diff;
					if (diff == null) {
						diff = patch.patch;
					}
					if (diff == null) {
						diff = patch.PATCH_DIFF_ORIG;
					}
					if (diff == null) {
						$http.get('benchmark/' + patch.benchmark + "/" + patch.bugId + "/" + "patch.diff").then(response => {
							patch.diff = response.data
							prepareDiff(response.data)
						}, error => {
							return $(elem).text('The diff is not available')
						})
					} else {
						prepareDiff(diff)
					}
				}
				scope.$watch('patch', function() {
					printDiff(scope.patch);
				})
				printDiff(scope.patch);
			}
		}
		}])
	.controller('welcomeController', function($uibModalInstance) {
		this.ok = function () {
			$uibModalInstance.close();
		};
	})
	.controller('bugModal', function($scope, $rootScope, $uibModalInstance, bug, classifications) {
		var $ctrl = this;
		$ctrl.bug = bug;
		$ctrl.classifications = classifications;

		$rootScope.$on('new_bug', function(e, bug) {
			$ctrl.bug = bug;
		});
		var getName = function (type, key) {
			for(var group in $ctrl.classifications[type]) {
				if ($ctrl.classifications[type][group][key]) {
					if ($ctrl.classifications[type][group][key].fullname) {
						return $ctrl.classifications[type][group][key].fullname;
					} else {
						return $ctrl.classifications[type][group][key].name;
					}
				}
			}
			return null
		}
		$ctrl.actionName = function (actionId) {
			return getName('Repair Actions', actionId);
		};
		$ctrl.patternName = function (patternId) {
			return getName('Repair Patterns', patternId);
		};
		$ctrl.ok = function () {
			$uibModalInstance.close();
		};
		$ctrl.nextPatch = function () {
			$rootScope.$emit('next_bug', 'next');
		};
		$ctrl.previousPatch = function () {
			$rootScope.$emit('previous_bug', 'next');
		};
	})
	.controller('bugController', function($scope, $location, $rootScope, $routeParams, $uibModal) {
		var $ctrl = $scope;
		$ctrl.bugs = $scope.$parent.filteredBugs;
		$ctrl.classifications = $scope.$parent.classifications;
		$ctrl.index = -1;
		$ctrl.bug = null;

		$scope.$watch("$parent.filteredBugs", function () {
			$ctrl.bugs = $scope.$parent.filteredBugs;
			$ctrl.index = getIndex($routeParams.benchmark, $routeParams.id);
		});
		$scope.$watch("$parent.classifications", function () {
			$ctrl.classifications = $scope.$parent.classifications;
		});

		var getIndex = function (benchmark, bugId) {
			if ($ctrl.bugs == null) {
				return -1;
			}
			for (var i = 0; i < $ctrl.bugs.length; i++) {
				if ($ctrl.bugs[i].benchmark == benchmark 
					&& ($ctrl.bugs[i].bugId == bugId || bugId == null)) {
					return i;
				}
			}
			return -1;
		};

		$scope.$on('$routeChangeStart', function(next, current) {
			$ctrl.index = getIndex(current.params.benchmark, current.params.id);
		});

		var modalInstance = null;
		$scope.$watch("index", function () {
			if ($scope.index != -1) {
				if (modalInstance == null) {
					modalInstance = $uibModal.open({
						animation: true,
						ariaLabelledBy: 'modal-title',
						ariaDescribedBy: 'modal-body',
						templateUrl: 'modelPatch.html',
						controller: 'bugModal',
						controllerAs: '$ctrl',
						size: "lg",
						resolve: {
							bug: function () {
								return $scope.bugs[$scope.index];
							},
							classifications: $scope.classifications
						}
					});
					modalInstance.result.then(function () {
						modalInstance = null;
						$location.path("/");
					}, function () {
						modalInstance = null;
						$location.path("/");
					})
				} else {
					$rootScope.$emit('new_bug', $scope.bugs[$scope.index]);
				}
			}
		});
		var nextPatch = function () {
			var index  = $scope.index + 1;
			if (index == $ctrl.bugs.length)  {
				index = 0;
			}
			$location.path( "/bug/" + $ctrl.bugs[index].benchmark + "/" + $ctrl.bugs[index].bugId );
			return false;
		};
		var previousPatch = function () {
			var index  = $scope.index - 1;
			if (index < 0) {
				index = $ctrl.bugs.length - 1;
			}
			$location.path( "/bug/" + $ctrl.bugs[index].benchmark + "/" + $ctrl.bugs[index].bugId );
			return false;
		};

		$scope.$on('keypress:39', function () {
			$scope.$apply(function () {
				nextPatch();
			});
		});
		$scope.$on('keypress:37', function () {
			$scope.$apply(function () {
				previousPatch();
			});
		});
		$rootScope.$on('next_bug', nextPatch);
		$rootScope.$on('previous_bug', previousPatch);
	})
	.controller('mainController', function($scope, $location, $rootScope, $http, $uibModal) {
		$scope.sortType     = ['benchmark', 'bugId']; // set the default sort type
		$scope.sortReverse  = false;
		$scope.match  = "all";
		$scope.filters = {};
		$scope.benchmarks = ["bears", "defects4J", /*"icse15",*/ "bugsjar", "bugswarm"];
		$scope.tools = ["NPEFix", "Nopol", "DynaMoth", "GenProg", "jGenProg", "Kali", "jKali", "Arja", "RSRepair", "Cardumen", "jMutRepair"];
		$scope.classifications = {};
		
		// create the list of sushi rolls 
		$scope.bugs = [];

		$http.get("www/data/classification.json").then(function (response) {
			$scope.classifications = response.data; 
		});

		function downloadPatches() {
			for (var bench of $scope.benchmarks) {
				$http.get("www/data/"+bench.toLowerCase() + ".json").then(function (response) {
					var bugs = response.data
		
					for (var key in bugs){
						$scope.bugs.push(bugs[key]);
					}
				});
			}
		}
		downloadPatches();
		var element = angular.element(document.querySelector('#menu')); 
					var element = angular.element(document.querySelector('#menu')); 
		var element = angular.element(document.querySelector('#menu')); 
		var height = element[0].offsetHeight;

		angular.element(document.querySelector('#mainTable')).css('height', (height-160)+'px');

		$scope.openBug = function (bug) {
			$location.path( "/bug/" + bug.benchmark + "/" + bug.bugId );
		};

		$scope.sort = function (sort) {
			if (sort == $scope.sortType || (sort[0] == 'benchmark' && $scope.sortType[0] == 'benchmark')) {
				$scope.sortReverse = !$scope.sortReverse; 
			} else {
				$scope.sortType = sort;
				$scope.sortReverse = false; 
			}
			return false;
		}

		$scope.countBugs = function (key, filter) {
			if (filter == null) {
				filter = {
				}
			}
			if (filter.count) {
				return filter.count;
			}
			var count = 0;
			for(var i = 0; i < $scope.bugs.length; i++) {
				if ($scope.bugs[i].benchmark.toLowerCase() === key.toLowerCase()) {
					count++;
				} else if ($scope.bugs[i].benchmark === key) {
					count++;
				} else if ($scope.bugs[i].repairActions && $scope.bugs[i].repairActions[key] != null && $scope.bugs[i].repairActions[key] > 0) {
					count++;
				} else if ($scope.bugs[i].repairPatterns && $scope.bugs[i].repairPatterns[key] != null && $scope.bugs[i].repairPatterns[key] > 0) {
					count++;
				}
			}
			filter.count = count;
			return count;
		};

		$scope.naturalCompare = function(a, b) {
			if (a.type === "number") {
				return a.value - b.value;
			}
			return naturalSort(a.value, b.value);
		}

		$scope.bugsFilter = function (bug, index, array) {
			var noneFilter = true;
			var hasBenchmark = false;
			var hasFilter = false;
			for (var filter in $scope.filters) {
				if ($scope.filters[filter] == false) {
					continue;
				}
				noneFilter = false;
				hasBenchmark = hasBenchmark || $scope.benchmarks.indexOf(filter) != -1;

				for(var group in $scope.classifications) {
					for (var i in $scope.classifications[group]) {
						for (var f in $scope.classifications[group][i]) {
							if (f == filter) {
								hasFilter = true
								break
							}
						}
						if (hasFilter) {
							break
						}
					}
					if (hasFilter) {
						break
					}
				}
			}
			if (noneFilter) {
				return true
			}
			var matchBenchmark = !hasBenchmark || Object.keys($scope.filters).filter(b => $scope.filters[b]).map(b => b.toLowerCase()).indexOf(bug.benchmark.toLowerCase()) > -1;

			
			var matchFilter = false;
			if (hasFilter) {
				for (var filter in $scope.filters) {
					if ($scope.filters[filter] != true) {
						continue
					}
					if (bug[filter] != null 
						|| ((bug.repairPatterns != null && bug.repairPatterns[filter] > 0) 
						|| (bug.repairActions && bug.repairActions[filter] > 0))) {
							matchFilter = true
					}
				}
			} else {
				matchFilter = true
			}

			return matchBenchmark && matchFilter;
		};
	});
