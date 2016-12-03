	// create the module and name it sharpicApp
	var sharpicApp = angular.module('sharpicApp', ['ngRoute']);

	// configure our routes
	sharpicApp.config(function($routeProvider) {
		$routeProvider
			.when('/', {
				templateUrl : 'pages/audits.html',
				controller  : 'mainController'
			})

			.when('/audits', {
				templateUrl : 'pages/audits.html',
				controller  : 'mainController'
			})

			.when('/sales', {
				templateUrl : 'pages/sales.html',
				controller  : 'mainController'
			})

			.when('/recipes', {
				templateUrl : 'pages/recipes.html',
				controller  : 'mainController'
			})

			.when('/products', {
				templateUrl : 'pages/products.html',
				controller  : 'mainController'
			})

			.when('/clients', {
				templateUrl : 'pages/clients.html',
				controller  : 'mainController'
			})

			.when('/reports', {
				templateUrl : 'pages/reports.html',
				controller  : 'mainController'
			})

			.when('/users', {
				templateUrl : 'pages/users.html',
				controller  : 'mainController'
			});
	});

	// create the controller and inject Angular's $scope
	sharpicApp.controller('mainController', function($rootScope, $http, $location, $route, $scope) {
		// create a message to display in our view
		$scope.message = 'Everyone come and see how good I look!';

        $scope.logout = function() {
            $http.post('logout', {}).finally(function() {
                $rootScope.authenticated = false;
                $location.path("/login");
            });
        };

	});

	sharpicApp.controller('aboutController', function($scope) {
		$scope.message = 'Look! I am an about page.';
	});

	sharpicApp.controller('contactController', function($scope) {
		$scope.message = 'Contact us! JK. This is just a demo.';
	});