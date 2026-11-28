package com.github.fabriciolfj.study.model;

import org.springaicommunity.agent.tools.TodoWriteTool;

import java.util.List;

public record TodoUpdateEvent(List<TodoWriteTool.Todos.TodoItem> todos) {
}
