/*
 * Copyright (C) 2019 Cricin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.vshape.lint;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastUtils;
import org.jetbrains.uast.visitor.AbstractUastVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 在activity中检测是否有调用 registerShapeFactory 或者registerActivity
 */
public final class InstalledBeforeSuperDetector extends Detector implements SourceCodeScanner, Detector.UastScanner{
    static final Issue ISSUE = Issue.create("ViewShapeInstalledBeforeSuper",
            "ViewShape registerActivity before super.onCreate" + "()",
            "You have installed ShapeHelper before super.onCreate()," +
            " this will cause AppCompatViews unavailable if you are using AppCompatActivity",
            Category.CORRECTNESS,
            7,
            Severity.WARNING,
            new Implementation(InstalledBeforeSuperDetector.class,Scope.JAVA_FILE_SCOPE));

    private static final String DISPOSE_METHOD = "disposeViewShape";
    private static final String INSTALL_METHOD = "registerActivity";
    private static final String NEW_METHOD = "registerShapeFactory";
    private static final String TYPE_SHAPE = "com.app.vshape.ShapeHelper";
    private static final String LEGACY_COMPAT_ACTIVITY = "android.support.v7.app.AppCompatActivity";
    private static final String COMPAT_ACTIVITY = "androidx.appcompat.app.AppCompatActivity";

    @Nullable
    @Override
    public List<String> getApplicableMethodNames(){
        //获取支持的方法集合
        ArrayList<String> strings = new ArrayList<>();
        strings.add(NEW_METHOD);
        strings.add(INSTALL_METHOD);
        strings.add(DISPOSE_METHOD);
        return strings;
    }

    @Override
    public void visitMethod(JavaContext context,UCallExpression call,PsiMethod method){
        JavaEvaluator evaluator = context.getEvaluator();

        //check ShapeHelper.registerActivity() call
        //下面的方法都是检测是否在AppCompatActivity onCreate中注册过registerActivity
        String methodName = method.getName();
        if(! methodName.equals(INSTALL_METHOD) || ! methodName.equals(NEW_METHOD) ||
           ! methodName.equals(DISPOSE_METHOD) || ! evaluator.isMemberInClass(method,TYPE_SHAPE))
            return;

        //check current class is decent of AppCompatActivity
        PsiClass legacyCompatActClass = evaluator.findClass(LEGACY_COMPAT_ACTIVITY);
        PsiClass compatActClass = evaluator.findClass(COMPAT_ACTIVITY);
        PsiClass c = UastUtils.getContainingClass(call);
        boolean isAppCompatActivity = false;
        if(c != null){
            isAppCompatActivity = (legacyCompatActClass != null && c.isInheritor(legacyCompatActClass,
                    true/*deep check*/)) || compatActClass != null && c.isInheritor(compatActClass,true/*deep check*/);
        }
        if(! isAppCompatActivity)
            return;

        //check current method is onCreate
        @SuppressWarnings("unchecked") UMethod uMethod = UastUtils.getParentOfType(call,true,UMethod.class);
        if(uMethod == null || ! "onCreate".equals(uMethod.getName()))
            return;

        SuperOnCreateFinder finder = new SuperOnCreateFinder(call);
        uMethod.accept(finder);
        if(! finder.isSuperOnCreateCalled()){
            context.report(ISSUE,
                    call,
                    context.getLocation(call),
                    "calling `ShapeHelper.registerActivity()` before super.onCreate can cause AppCompatViews unavailable");
        }
    }

    private static class SuperOnCreateFinder extends AbstractUastVisitor{
        /**
         * The target registerShapeFactory call
         */
        private final UCallExpression target;
        /**
         * Whether we've found the super.onCreate() before ShapeHelper installed
         */
        private boolean ok;
        /**
         * Whether we've seen the target super.onCreate() node yet
         */
        private boolean onCreateFound;

        private SuperOnCreateFinder(UCallExpression target){
            this.target = target;
        }

        @Override
        public boolean visitCallExpression(UCallExpression node){
            if(node == target || node.getPsi() != null && node.getPsi() == target.getPsi()){
                if(onCreateFound){
                    ok = true;
                    return true;
                }
            } else{
                if("onCreate".equals(LintUtils.getMethodName(node)) && node.getReceiver() != null && "super".equals(node
                        .getReceiver()
                        .toString()))
                {
                    onCreateFound = true;
                }
            }
            return super.visitCallExpression(node);
        }

        boolean isSuperOnCreateCalled(){
            return ok;
        }
    }
}
