/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.iotdb.flink;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.fileSystem.FSFactoryProducer;
import org.apache.iotdb.tsfile.write.TsFileWriter;
import org.apache.iotdb.tsfile.write.record.TSRecord;
import org.apache.iotdb.tsfile.write.record.datapoint.DataPoint;
import org.apache.iotdb.tsfile.write.record.datapoint.LongDataPoint;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.apache.iotdb.tsfile.write.schema.Schema;

import java.io.File;

/**
 * Utils used to prepare source TsFiles for the examples.
 */
public class TsFlieUtils {

	private static final String DEFAULT_TEMPLATE = "template";

	public static void writeTsFile(String path) {
		try {
			File f = FSFactoryProducer.getFSFactory().getFile(path);
			if (f.exists()) {
				f.delete();
			}
			Schema schema = new Schema();
			schema.extendTemplate(DEFAULT_TEMPLATE, new MeasurementSchema("sensor_1", TSDataType.FLOAT, TSEncoding.RLE));
			schema.extendTemplate(DEFAULT_TEMPLATE, new MeasurementSchema("sensor_2", TSDataType.INT32, TSEncoding.TS_2DIFF));
			schema.extendTemplate(DEFAULT_TEMPLATE, new MeasurementSchema("sensor_3", TSDataType.INT32, TSEncoding.TS_2DIFF));

			TsFileWriter tsFileWriter = new TsFileWriter(f, schema);

			// construct TSRecord
			for (int i = 0; i < 100; i++) {
				TSRecord tsRecord = new TSRecord(i, "device_" + (i % 4));
				DataPoint dPoint1 = new LongDataPoint("sensor_1", i);
				DataPoint dPoint2 = new LongDataPoint("sensor_2", i);
				DataPoint dPoint3 = new LongDataPoint("sensor_3", i);
				tsRecord.addTuple(dPoint1);
				tsRecord.addTuple(dPoint2);
				tsRecord.addTuple(dPoint3);

				// write TSRecord
				tsFileWriter.write(tsRecord);
			}

			tsFileWriter.close();
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
