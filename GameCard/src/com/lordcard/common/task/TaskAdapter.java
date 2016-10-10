package com.lordcard.common.task;

import com.lordcard.common.task.base.TaskListener;
import com.lordcard.common.task.base.TaskResult;

/**
 * 任务事件监听抽象类 common.task.TaskAdapter
 * 
 * @author yinhb <br/>
 *         create at 2013 2013-2-5 下午4:11:49
 */
public abstract class TaskAdapter implements TaskListener {

	public abstract String getName();

	public void onPreExecute(GenericTask task) {};

	public void onPostExecute(GenericTask task, TaskResult result) {};

	public void onProgressUpdate(GenericTask task, Object param) {};

	public void onCancelled(GenericTask task) {};
}
