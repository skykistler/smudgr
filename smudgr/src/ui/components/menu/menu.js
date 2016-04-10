function MenuCtrl($scope, $http, smudgr) {
  $http.get("components/menu/menu.json")
    .then(function(response) {
      $scope.menuBar = response.data;
    }
  );

  $scope.action = function (command) {
    smudgr.exec(command);
  };
}

angular.module('smudgr').component('smudgrMenu', {
  templateUrl: "components/menu/menu.html",
  controller: MenuCtrl
});
