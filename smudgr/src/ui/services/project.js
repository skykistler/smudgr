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
    console.log(data);
  }

  getSmudge() {
    if (this.data && this.data.smudges && this.data.smudges.length > 0)
      return this.data.smudges[0];
    else
      return {};
  }

  getCurrentElement() {
    return this.currentElement;
  }

}

angular.module('smudgr').service('project', project);
