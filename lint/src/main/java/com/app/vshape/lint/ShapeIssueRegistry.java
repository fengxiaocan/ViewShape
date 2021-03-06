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

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import com.app.vshape.dom.ShapeDomExtender;
import com.app.vshape.preview.ShapePreview;

import java.util.Collections;
import java.util.List;

/**
 * 安装当前插件
 */
@SuppressWarnings("unused")
public final class ShapeIssueRegistry extends IssueRegistry{


    static{
        installStudioPlugins();
    }

    private static void installStudioPlugins(){
        boolean lintInsideStudio;
        try{
            Class.forName("org.jetbrains.android.dom.AndroidDomElement");
            lintInsideStudio=true;
        } catch(ClassNotFoundException e){
            lintInsideStudio=false;
        }
        if(lintInsideStudio){
            try{
                ShapeDomExtender.install();
            } catch(Exception ignore){
            }
            try{
                ShapePreview.install();
            } catch(Exception ignore){
            }
        }
    }

    @Override
    public List<Issue> getIssues(){
        return Collections.singletonList(InstalledBeforeSuperDetector.ISSUE);
    }

    @Override
    public int getApi(){
        return ApiKt.CURRENT_API;
    }
}
