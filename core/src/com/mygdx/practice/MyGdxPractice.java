package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MyGdxPractice extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
//	private Texture img;
	private Texture test;
	private Texture arror;
	private OrthographicCamera camera;
	private Stage stage;

	@Override
	public void create () {
//		img = new Texture("Elsa_0_mirror.bmp");
		test = new Texture("lady_beetle.png");
		arror = new Texture("arrow.png");
		camera = new OrthographicCamera(1000, 500);
		batch = new SpriteBatch();

		Image image = new Image(test);
		image.addAction(Actions.moveTo(camera.viewportWidth / 2, camera.viewportHeight / 2));
		stage = new Stage();
		stage.addActor(image);

		InputMultiplexer inputMultiplexer = new InputMultiplexer(this, stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1f, 1, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		batch.begin();

//		TextureRegion a = new TextureRegion(img, 80, 80);

//		batch.draw(a, 0, 0);
		batch.draw(arror,
				-camera.viewportWidth / 2 + arror.getWidth() + 24, -camera.viewportHeight / 2, // 圖片左下角的點
                0, 0, // 操作的中心點???
                arror.getWidth(), arror.getHeight(), // 渲染出來的寬高
                1f, 1f,
                0f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                arror.getWidth(), arror.getHeight(), // 操作目標的寬高
                false, false);

		batch.draw(arror,
				-camera.viewportWidth / 2 + arror.getWidth() + 12, -camera.viewportHeight / 2, // 圖片左下角的點
				0, arror.getHeight() / 2, // 操作的中心點???
				arror.getWidth(), arror.getHeight(), // 渲染出來的寬高
				1f, 1f,
				180f,  // 逆時針旋轉 角度
				0, 0,  // 操作目標的起始點
				arror.getWidth(), arror.getHeight(), // 操作目標的寬高
				false, false);


		batch.end();

		stage.act();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		arror.dispose();
//		img.dispose();
		test.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		System.out.println(String.format("keyDown: %d", keycode));
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		System.out.println(String.format("keyUp: %d", keycode));
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float x = -camera.viewportWidth / 2 + arror.getWidth() + 24;
		float y = -camera.viewportHeight / 2;
		Rectangle rectangle = new Rectangle(x, y, arror.getWidth(), arror.getHeight());
		Vector3 vec = new Vector3(screenX, screenY, 0);
//		Vector3 size = camera.project(new Vector3(arror.getWidth(), arror.getHeight(), 0));
		camera.unproject(vec);
		System.out.println(String.format("vec: x1->%f, y1->%f", vec.x, vec.y));
		if (rectangle.contains(vec.x, vec.y)) {
			Gdx.app.log("test", "contains arrow");
			Actor test = stage.getActors().first();
			test.moveBy(10, 0);
//			camera.translate(10, 0);
		}

		System.out.println(String.format("rectangle: x1->%f, y1->%f, x2->%f, y2->%f", rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height));
		System.out.println(String.format("touchDown: x->%d, y->%d, p->%d, b->%d", screenX, screenY, pointer, button));
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
