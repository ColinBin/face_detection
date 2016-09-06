package com.extreme.facedetection.fragment;

import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.extreme.facedetection.Config;
import com.extreme.facedetection.R;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class CollectFaceDataFragment extends Fragment implements
		OnClickListener {

	HttpRequests httpRequests = new HttpRequests(Config.API_KEY,
			Config.API_SECRET, true, false);
	Boolean isPhotoTaken = false;
	Boolean isPhotoDetected = false;
	Boolean isPhotoNew = false;
	private Button getPhoto;
	private Button detectFace;
	private Button generateData;

	private Button identifyFace;

	private ImageView imageView;
	private Bitmap img = null;
	private int faceCount = 0;
	private String studentNumber = "";
	private String face_id;

	byte[] imageArray;

	private LayoutInflater inflater;

	private View view;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				TrainGroup();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.collect_face_data, container, false);
		this.inflater = inflater;

		Bundle bundle = this.getArguments();
		studentNumber = bundle.getString("student_number");

		getPhoto = (Button) view.findViewById(R.id.get_photo);
		getPhoto.setOnClickListener(this);
		detectFace = (Button) view.findViewById(R.id.detect_face);
		detectFace.setOnClickListener(this);
		generateData = (Button) view.findViewById(R.id.generate_data);
		generateData.setOnClickListener(this);

		identifyFace = (Button) view.findViewById(R.id.identify_face);
		identifyFace.setOnClickListener(this);

		imageView = (ImageView) view.findViewById(R.id.image);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_photo:
			Intent toTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(toTakePhoto, 1);
			break;
		case R.id.detect_face:
			if (isPhotoTaken) {
				DetectFace();
			} else {
				Toast.makeText(inflater.getContext(), "��������...",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.generate_data:

			if (!isPhotoTaken) {
				Toast.makeText(inflater.getContext(), "��������...",
						Toast.LENGTH_SHORT).show();
			} else if (!isPhotoDetected) {
				Toast.makeText(inflater.getContext(), "����ʶ��...",
						Toast.LENGTH_SHORT).show();
			} else if (!isPhotoNew) {
				Toast.makeText(inflater.getContext(), "����ʹ������һ������Ƭ...\n����������",
						Toast.LENGTH_SHORT).show();
			} else if (faceCount == 0) {
				Toast.makeText(inflater.getContext(), "δʶ������\n����������",
						Toast.LENGTH_SHORT).show();
			} else if (faceCount > 1) {
				Toast.makeText(inflater.getContext(), "ʶ�𵽶�������\n����������",
						Toast.LENGTH_SHORT).show();
			} else {
				GenerateData();
			}

			break;

		case R.id.identify_face:
			if (!isPhotoTaken) {
				Toast.makeText(inflater.getContext(), "��������...",
						Toast.LENGTH_SHORT).show();
			} else {
				IdentifyFace();
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// the image picker callback
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = intent.getExtras();
			isPhotoTaken = true;
			isPhotoDetected = false;
			isPhotoNew = true;
			img = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ
			imageView.setImageBitmap(img);// ��ͼƬ��ʾ��ImageView��
		}
	}

	private Bitmap getNewBitmap(Bitmap bitmap) {
		// ���ͼƬ�Ŀ��
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// ������Ҫ�Ĵ�С
		// int newWidth = width*4;
		// int newHeight = 480;
		// �������ű���
		// float scaleWidth = ((float) newWidth) / width;
		// float scaleHeight = ((float) newHeight) / height;
		// ȡ����Ҫ���ŵ�matrix����
		Matrix matrix = new Matrix();
		matrix.postScale(6, 6);
		// �õ��µ�ͼƬ
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	private class FaceppDetect {
		DetectCallback callback = null;

		public void setDetectCallback(DetectCallback detectCallback) {
			callback = detectCallback;
		}

		public void detect(final Bitmap image) {

			new Thread(new Runnable() {
				@Override
				public void run() {

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					imageArray = stream.toByteArray();

					try {
						// detect
						JSONObject result = httpRequests
								.detectionDetect(new PostParameters()
										.setImg(imageArray));
						// finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);
						}
					} catch (FaceppParseException e) {
						e.printStackTrace();
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(inflater.getContext(), "�������",
										Toast.LENGTH_SHORT).show();
							}
						});
					}

				}
			}).start();
		}
	}

	interface DetectCallback {
		void detectResult(JSONObject rst);
	}

	private void DetectFace() {
		FaceppDetect faceppDetect = new FaceppDetect();
		faceppDetect.setDetectCallback(new DetectCallback() {

			public void detectResult(JSONObject rst) {
				// Log.v(TAG, rst.toString());
				isPhotoDetected = true;
				try {
					face_id = rst.getJSONArray("face").getJSONObject(0)
							.getString("face_id");
					System.out.println(face_id);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				// use the red paint
				Paint paint = new Paint();
				paint.setColor(Color.RED);
				paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

				// create a new canvas
				Bitmap bitmap = Bitmap.createBitmap(img.getWidth(),
						img.getHeight(), img.getConfig());
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(img, new Matrix(), null);

				try {
					// find out all faces
					final int count = rst.getJSONArray("face").length();
					faceCount = count;
					for (int i = 0; i < count; ++i) {
						float x, y, w, h;
						// get the center point
						x = (float) rst.getJSONArray("face").getJSONObject(i)
								.getJSONObject("position")
								.getJSONObject("center").getDouble("x");
						y = (float) rst.getJSONArray("face").getJSONObject(i)
								.getJSONObject("position")
								.getJSONObject("center").getDouble("y");

						// get face size
						w = (float) rst.getJSONArray("face").getJSONObject(i)
								.getJSONObject("position").getDouble("width");
						h = (float) rst.getJSONArray("face").getJSONObject(i)
								.getJSONObject("position").getDouble("height");

						// change percent value to the real size
						x = x / 100 * img.getWidth();
						w = w / 100 * img.getWidth() * 0.7f;
						y = y / 100 * img.getHeight();
						h = h / 100 * img.getHeight() * 0.7f;

						// draw the box to mark it out
						canvas.drawLine(x - w, y - h, x - w, y + h, paint);
						canvas.drawLine(x - w, y - h, x + w, y - h, paint);
						canvas.drawLine(x + w, y + h, x - w, y + h, paint);
						canvas.drawLine(x + w, y + h, x + w, y - h, paint);
					}

					// save new image
					img = bitmap;

					getActivity().runOnUiThread(new Runnable() {

						public void run() {
							// show the image
							imageView.setImageBitmap(img);
							Toast.makeText(inflater.getContext(),
									"ʶ���" + count + "������", Toast.LENGTH_SHORT)
									.show();
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(inflater.getContext(), "Error",
									Toast.LENGTH_SHORT).show();
						}
					});
				}

			}
		});
		faceppDetect.detect(img);
	}

	private void GenerateData() {
		new Thread(new Runnable() {
			JSONObject result = null;
			boolean tag_is_person = false;
			boolean tag_is_group = false;

			@Override
			public void run() {
				try {
					try {
						result = httpRequests.infoGetGroupList();
						JSONArray groupArray = result.getJSONArray("group");
						for (int i = 0; i < groupArray.length(); i++) {
							if (groupArray.getJSONObject(i)
									.getString("group_name").equals("my_group")) {
								tag_is_group = true;
								break;
							}
						}
						if (!tag_is_group) {
							httpRequests.groupCreate(new PostParameters()
									.setGroupName("my_group"));
						}

						result = httpRequests
								.infoGetPersonList(new PostParameters()
										.setGroupName("my_group"));
						System.out.println("infoGetPersonList" + result);
						Log.i("hhh", result.toString());
						JSONArray arrayPerson = result.getJSONArray("person");
						for (int i = 0; i < arrayPerson.length(); i++) {
							if (arrayPerson.getJSONObject(i)
									.getString("person_name")
									.equals(studentNumber)) {
								tag_is_person = true;
								break;
							}
						}
						if (!tag_is_person) { // ��δ����person
							httpRequests.personCreate(new PostParameters()
									.setPersonName(studentNumber));

							httpRequests.groupAddPerson(new PostParameters()
									.setGroupName("my_group").setPersonName(
											studentNumber));
						}

						if (face_id == null) {
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(inflater.getContext(),
											"���µ�������Ϣ�������¿�ʼ�ɼ�",
											Toast.LENGTH_SHORT).show();
								}
							});
							return;
						}
						httpRequests.personAddFace(new PostParameters()
								.setPersonName(studentNumber)
								.setFaceId(face_id));

						isPhotoNew = false;
						Message startTrainGroup = new Message();
						startTrainGroup.what = 200;
						handler.sendMessage(startTrainGroup);

						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(inflater.getContext(), "�����ɼ��ɹ�",
										Toast.LENGTH_SHORT).show();
							}
						});

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FaceppParseException e) {
					// TODO Auto-generated catch block
					Log.i("hhh", e.toString());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(inflater.getContext(),
									"�����쳣����ȷ�Ϻ�����", Toast.LENGTH_SHORT).show();
						}
					});
				}

			}
		}).start();

	}

	private void TrainGroup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject syncRet = null;
				try {
					syncRet = httpRequests.trainIdentify(new PostParameters()
							.setGroupName("my_group"));
					System.out.println("train group session:"
							+ syncRet.toString());

				} catch (FaceppParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).start();

	}

	private void IdentifyFace() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		float scale = Math.min(1,
				Math.min(600f / img.getWidth(), 600f / img.getHeight()));
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
				img.getHeight(), matrix, false);
		// Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " "
		// + imgSmall.getHeight());

		imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		imageArray = stream.toByteArray();
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject identifyResult = null;
				try {
					identifyResult = httpRequests
							.recognitionIdentify(new PostParameters()
									.setGroupName("my_group")
									.setImg(imageArray));
					System.out.println(identifyResult.toString());
					JSONObject bestCandidate;
					JSONArray resultArray;
					resultArray = identifyResult.getJSONArray("face");
					if (resultArray.length() == 0) {
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(inflater.getContext(),
										"ʶ��ʧ�ܣ�������ʶ��", Toast.LENGTH_SHORT)
										.show();
							}
						});
					} else if (resultArray.length() > 1) {
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(inflater.getContext(),
										"ʶ�𵽶�������\n����������", Toast.LENGTH_SHORT)
										.show();
							}
						});
					} else {
						bestCandidate = identifyResult.getJSONArray("face")
								.getJSONObject(0).getJSONArray("candidate")
								.getJSONObject(0);

						if (bestCandidate.getDouble("confidence") < 50) {
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(inflater.getContext(),
											"����������", Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							studentNumber = bestCandidate
									.getString("person_name");
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(inflater.getContext(),
											"ʶ��ɹ�:" + studentNumber,
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					}

				} catch (FaceppParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}
}
