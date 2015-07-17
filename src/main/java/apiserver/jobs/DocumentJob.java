package apiserver.jobs;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.core.model.IDocument;

import java.io.Serializable;

/**
 * User: mikenimer
 * Date: 10/24/13
 */
public class DocumentJob implements Serializable
{
    private IDocument document;

    public IDocument getDocument()
    {
        return document;
    }

    public void setDocument(IDocument document)
    {
        this.document = document;
    }

}
