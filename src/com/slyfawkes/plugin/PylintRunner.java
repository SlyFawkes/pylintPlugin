package com.slyfawkes.plugin;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilderFactoryImpl;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PylintRunner extends AnAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(PylintRunner.class);

    @Override
    public void actionPerformed(final AnActionEvent anActionEvent) {
        LOGGER.info("Things are happening");
        Project project = anActionEvent.getProject();

        if (project != null) {
            project.save();
            String path = project.getBasePath();
            LOGGER.info(path);

            runPylint(path, project);
        }
    }

    private void runPylint(final String path, final Project project) {
        TextConsoleBuilderFactory textConsoleBuilderFactory = new TextConsoleBuilderFactoryImpl();
        TextConsoleBuilder textConsoleBuilder = textConsoleBuilderFactory.createBuilder(project);
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        textConsoleBuilder.setViewer(true);
        ConsoleView consoleView = textConsoleBuilder.getConsole();
        try {
            manager.registerToolWindow("pylint", consoleView.getComponent(), ToolWindowAnchor.BOTTOM);
            GeneralCommandLine generalCommandLine = new GeneralCommandLine("pylint");
            generalCommandLine.addParameter(path);
            LOGGER.info(generalCommandLine.getCommandLineString());

            OSProcessHandler osProcessHandler = new OSProcessHandler(generalCommandLine);
            consoleView.attachToProcess(osProcessHandler);
            osProcessHandler.startNotify();


            LOGGER.info(Integer.toString(consoleView.getContentSize()));
            LOGGER.info(consoleView.toString());

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
