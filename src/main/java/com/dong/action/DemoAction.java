package com.dong.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, editor.getProject());
        //只读文件直接返回
        if (psiFile.getFileType().isReadOnly()) {
            return;
        }

        Document document = PsiDocumentManager.getInstance(event.getProject()).getDocument(psiFile);

        for (PsiElement psiElement : psiFile.getChildren()) {
            if (psiElement instanceof PsiClass) {
                // 获取类上面的RequestMapping注解信息
                PsiClass psiClass = (PsiClass) psiElement;

                PsiField[] fields = psiClass.getFields();
                for (PsiField field : fields) {
                    String s = checkComment(document, field);
                    System.out.println(s);
                }
            }
        }
    }

    /**
     * 获取注释
     *
     * @param document
     * @param psiElement
     * @return
     */
    private String checkComment(Document document, PsiElement psiElement) {
        String comment = "";
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiElement.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                classComment = (PsiComment) tmpEle;
                // 注释的内容
                String tmpText = classComment.getText();

                String pattern = "[\\u4E00-\\u9FA5A-Za-z0-9]+";

                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(tmpText);
                while (m.find()) {
                    comment = m.group(0);
                    break;
                }
            }
        }
        return comment;
    }
}
