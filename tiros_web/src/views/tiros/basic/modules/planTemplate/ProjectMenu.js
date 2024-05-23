const ProjectMenu = function () {
  ProjectMenu.superclass.constructor.call(this);
}

mini.extend(ProjectMenu, mini.Menu, {
  _create: function () {
    ProjectMenu.superclass._create.call(this);

    const menuItems = [
      { type: "该行前-新增任务", text: '该行前-新增任务', name: "add_task_before" },
      { type: "该行前-新增任务", text: '该行后-新增任务', name: "add_task_after" },
      { type: "该行下-新增子任务", text: '该行下-新增子任务', name: "add_children" },
      '-',
      { type: "修改任务", text: '修改任务', name: "edit_task" },
      { type: "删除任务", text: '删除任务', name: "delete_task" },
    ];
    this.setItems(menuItems);
    this.editTask= mini.getbyName("edit_task", this);
    this.deleteTask = mini.getbyName("delete_task", this);
    this.beforeAddTask = mini.getbyName("add_task_before", this);
    this.afterAddTask = mini.getbyName("add_task_after", this);
    this.addChildren = mini.getbyName("add_children", this);
  }
});

export default ProjectMenu;