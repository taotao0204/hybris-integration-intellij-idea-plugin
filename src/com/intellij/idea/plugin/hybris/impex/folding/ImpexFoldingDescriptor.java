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

package com.intellij.idea.plugin.hybris.impex.folding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created 22:34 01 January 2015
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class ImpexFoldingDescriptor extends FoldingDescriptor {

    private final String placeholder;

    public ImpexFoldingDescriptor(@NotNull final PsiElement psiElement,
                                  @NotNull final FoldingGroup group) {
        super(
                psiElement.getNode(),
                new TextRange(
                        psiElement.getTextRange().getStartOffset(),
                        psiElement.getTextRange().getEndOffset()
                ),
                group
        );

        placeholder = ImpexFoldingPlaceholderBuilderFactory.getPlaceholderBuilder().getPlaceholder(psiElement);
    }

    @Nullable
    @Override
    public String getPlaceholderText() {
        return placeholder;
    }
}