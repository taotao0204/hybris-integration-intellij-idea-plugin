/*
 * Copyright 2015 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.idea.plugin.hybris.impex.utils;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created 4:20 PM 31 May 2015
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class CommonPsiUtils {

    @Nullable
    @Contract(pure = true)
    public static IElementType getNullSafeElementType(@Nullable final PsiElement element) {
        return element == null ? null : CommonAstUtils.getNullSafeElementType(element.getNode());
    }

    @Nullable
    @Contract(pure = true)
    public static PsiElement getNextNonWhitespaceElement(@NotNull final PsiElement element) {
        PsiElement nextSibling = element.getNextSibling();

        while (null != nextSibling && ImpexPsiUtils.isWhiteSpace(nextSibling)) {
            nextSibling = nextSibling.getNextSibling();
        }

        return nextSibling;
    }

    @NotNull
    @Contract(pure = true)
    public static List<PsiElement> findChildrenByIElementType(@NotNull final PsiElement element,
                                                              @NotNull final IElementType elementType) {
        List<PsiElement> result = Collections.emptyList();
        ASTNode child = element.getNode().getFirstChildNode();

        while (child != null) {
            if (elementType == child.getElementType()) {
                if (null == result || result.isEmpty()) {
                    result = new ArrayList<PsiElement>();
                }
                result.add(child.getPsi());
            }
            child = child.getTreeNext();
        }

        return result;
    }

}
