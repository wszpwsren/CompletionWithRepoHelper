package com.github.wszpwsren.completionwithrepohelper.services

import com.intellij.openapi.project.Project
import com.github.wszpwsren.completionwithrepohelper.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
