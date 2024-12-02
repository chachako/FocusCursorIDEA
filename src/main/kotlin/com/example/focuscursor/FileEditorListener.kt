package com.example.focuscursor

import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.project.Project
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.TextEditor

class FileEditorListener : FileEditorManagerListener {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val mediaType = "application/json; charset=utf-8".toMediaType()
    
    data class ProjectInfo(
        val project: String,
        val file: String,
        val line: Int
    )

    override fun selectionChanged(event: FileEditorManagerEvent) {
        val project = event.manager.project
        val newFile = event.newFile ?: return
        
        val projectPath = project.projectFilePath?.replace("/.idea/misc.xml", "") ?: return
        val projectName = projectPath.split("/").last()

        // 获取当前编辑器
        val editor = (event.newEditor as? TextEditor)?.editor ?: return
        // 获取当前光标位置的行号（0-based，所以加1）
        val currentLine = editor.caretModel.primaryCaret.logicalPosition.line + 1

        val projectInfo = ProjectInfo(
            project = projectName,
            file = newFile.path,
            line = currentLine
        )

        val requestBody = RequestBody.create(
            mediaType,
            gson.toJson(projectInfo)
        )

        val request = Request.Builder()
            .url("http://127.0.0.1:8989/focus") // 请替换为您的服务器URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理错误
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    // 处理响应
                }
            }
        })
    }
} 