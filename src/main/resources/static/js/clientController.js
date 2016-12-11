sharpicApp.controller('clientController', function($rootScope, $http, $location, $route, $scope) {
    $scope.clients = null;

    $scope.getClients = function() {
        $http.get('/client/getClients')
            .success(function (data, status, headers, config) {
            $scope.clients = data;
        })
        .error(function (data, status, header, config) {
        });
     };

    $scope.getClients();

    $scope.getLocationNames = function(client) {
        var locationNamesStr = "";

        var locations = client.locations;
        if(locations!=null) {
            for( var i=0; i<locations.length; i++) {
                locationNamesStr += locations[i].locatioName;
                if(i!=(locations.length-1))
                    locationNamesStr += "<br>";
            }
        }

        return locationNamesStr;
    };

    $scope.getModifierNames = function(client) {
        var modifierNamesStr = "";

        var modifiers = client.modifiers;
        if(modifiers!=null) {
            for( var i=0; i<modifiers.length; i++) {
                modifierNamesStr += modifiers[i].modifierName;
                if(i!=(modifiers.length-1))
                    modifierNamesStr += "<br>";
            }
        }

        return modifierNamesStr;
    };

});