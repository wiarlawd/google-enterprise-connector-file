// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.filenet4;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import com.google.enterprise.connector.filenet4.api.IBaseObject;
import com.google.enterprise.connector.filenet4.api.MockBaseObject;
import com.google.enterprise.connector.filenet4.api.MockObjectStore;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.DatabaseType;
import com.filenet.api.constants.VersionStatus;
import com.filenet.api.core.Document;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.events.DeletionEvent;
import com.filenet.api.util.Id;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * These mocks make a terrible but convenient assumption that the
 * VersionSeries ID is the same as the ID of the Document or
 * DeletionEvent that refers to it. That means that we can check the
 * PROPNAME_DOCID of a DocumentList against the Document or
 * DeletionEvent ID. On the downside, it means that we can't have a
 * Document and a DeletionEvent referring to the same VersionSeries in
 * the object store at the same time. The {@link #generateObjectMap}
 * enforces that by preferring existing keys, which in practice means
 * preferring documents over deletion events.
 */
class ObjectMocks {
  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  private static Date parseTime(String timeStr) {
    try {
      return dateFormatter.parse(timeStr);
    } catch (ParseException e) {
      throw new AssertionError(e);
    }
  }

  public static IBaseObject newBaseObject(MockObjectStore objectStore,
      String guid, String timeStr, boolean isReleasedVersion) {
    return newBaseObject(objectStore, guid, timeStr, isReleasedVersion,
        new AccessPermissionListMock());
  }

  public static IBaseObject newBaseObject(MockObjectStore objectStore,
      String guid, String timeStr, boolean isReleasedVersion,
      AccessPermissionList perms) {
    IBaseObject object = new MockBaseObject(
        mockDocument(guid, timeStr, isReleasedVersion, perms),
        isReleasedVersion);
    objectStore.addObject(object);
    return object;
  }

  public static IBaseObject newDeletionEvent(MockObjectStore objectStore,
      String guid, String timeStr, boolean isReleasedVersion) {
    IBaseObject object = new MockBaseObject(mockDeletionEvent(guid, timeStr),
        isReleasedVersion);
    objectStore.addObject(object);
    return object;
  }

  private static Document mockDocument(String guid, String timeStr,
      boolean isReleasedVersion, AccessPermissionList perms) {
    VersionSeries vs = createMock(VersionSeries.class);
    expect(vs.get_Id()).andStubReturn(new Id(guid));
    Document doc = createMock(Document.class);
    expect(doc.get_Id()).andStubReturn(new Id(guid));
    expect(doc.get_VersionSeries()).andStubReturn(vs);
    expect(doc.get_DateLastModified()).andStubReturn(parseTime(timeStr));
    expect(doc.get_CurrentVersion()).andStubReturn(doc);
    expect(doc.get_ReleasedVersion()).andStubReturn(
        isReleasedVersion ? doc : null);
    expect(doc.get_VersionStatus()).andStubReturn(
        isReleasedVersion ? VersionStatus.RELEASED : VersionStatus.SUPERSEDED);
    expect(doc.get_Permissions()).andStubReturn(perms);
    replay(vs, doc);
    return doc;
  }

  private static DeletionEvent mockDeletionEvent(String guid, String timeStr) {
    DeletionEvent event = createMock(DeletionEvent.class);
    expect(event.get_Id()).andStubReturn(new Id(guid));
    expect(event.get_VersionSeriesId()).andStubReturn(new Id(guid));
    expect(event.get_DateCreated()).andStubReturn(parseTime(timeStr));
    replay(event);
    return event;
  }

  public static MockObjectStore newObjectStore(DatabaseType dbType)  {
    return new MockObjectStore(dbType);
  }
}
