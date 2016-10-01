class project {

  constructor(smudgr) {
    this.smudgr = smudgr;
    smudgr.addHandler('project', this.set, this)
  }

  new() {
    this.smudgr.exec('project.new');
  }

  open() {
    this.smudgr.exec('project.open');
  }

  save(as) {
    this.smudgr.exec('project.save', { as: as ? true : false });
  }

  set(data) {
    this.data = data.project;
    this.smudge = this.data.smudges[0];
    console.log(data);
  }

  getCurrentElement() {
    return this.currentElement;
  }

}

angular.module('smudgr').service('project', project);
