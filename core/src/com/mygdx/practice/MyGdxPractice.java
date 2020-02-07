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
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.practice.component.Arrow;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.Mario;

public class MyGdxPractice extends ApplicationAdapter {
//	private SpriteBatch batch;
//	private Texture img;
	private Texture test;
	private UserController arror;
	private OrthographicCamera camera;
//	private OrthographicCamera fixCamera;
	private Stage stage;

	private TiledMap map;
	private TiledMapRenderer mapRender;

	private World world;
	private Box2DDebugRenderer box2dRender;
	private Body mainBody;
	private static final int width = 1000;
	private static final int height = 500;

	@Override
	public void create () {
		test = new Texture("lady_beetle.png");
		arror = new UserController("arrow.png", "up.png");
		camera = new OrthographicCamera(width, height);
		world = new World(new Vector2(0, -9.8f), true);
		box2dRender = new Box2DDebugRenderer();

		arror.setCameraViewport(width, height);
		arror.addOnTouchListener(new UserController.OnTouchListener() {
			@Override
			public void onTouchRight(int pointer) {

//				camera.translate(2, 0, 0);
			}

			@Override
			public void onTouchLeft(int pointer) {

				if (camera.position.x < 500f) {
					camera.position.x = 500f;
					return;
				} else if (camera.position.x == 500f) {
					return;
				}
				camera.translate(-2, 0, 0);
			}

            @Override
            public void onJump(int pointer) {

            }
        });
		TmxMapLoader mapLoader = new TmxMapLoader();
		map = mapLoader.load("map/test.tmx");

		mapRender = new OrthogonalTiledMapRenderer(map);

//		System.out.println(String.format("map object size: %d", map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).size));

		RectangleMapObject mapObject = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).get(0);
		Rectangle rect = mapObject.getRectangle();
		final Image image = new Image(test);
		image.addAction(Actions.moveTo(rect.getX() + image.getWidth(), rect.getY() + rect.height));
		Viewport viewport = new ScalingViewport(Scaling.fill, 1000, 500, camera);
		stage = new Stage(viewport);
		stage.addActor(image);
//		camera.position.set()

		InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, arror);
        Gdx.input.setInputProcessor(inputMultiplexer);

		BodyDef bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

		Body body = world.createBody(bdef);
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef);

		for (RectangleMapObject mapObj: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			rect = mapObj.getRectangle();
			bdef = new BodyDef();
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

			body = world.createBody(bdef);
			fixtureDef = new FixtureDef();
			fixtureDef.friction = 0f;
			shape = new PolygonShape();
			shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
		}

		bdef = new BodyDef();
		bdef.type = BodyDef.BodyType.DynamicBody;
		bdef.position.set(image.getX() + 50, image.getY() + 100);


//		create 主角
		mainBody = world.createBody(bdef);
		fixtureDef = new FixtureDef();
		CircleShape shape1 = new CircleShape();
		shape1.setRadius(16);
		fixtureDef.density = 0.001f;
		fixtureDef.shape = shape1;
		fixtureDef.friction = 0.4f;
		mainBody.createFixture(fixtureDef);
		image.addAction(Actions.moveTo(mainBody.getPosition().x, mainBody.getPosition().y));
//		image.setPosition();

		arror.addOnTouchListener(new UserController.OnTouchListener() {
			@Override
			public void onTouchRight(int pointer) {
				if (Math.abs(mainBody.getLinearVelocity().x) < 32) {
					if (mainBody.getLinearVelocity().y != 0) {
//						mainBody.applyLinearImpulse(new Vector2(4f, mainBody.getLinearVelocity().y), mainBody.getWorldCenter(), true);
					} else {
						mainBody.applyLinearImpulse(new Vector2(4f, 0), mainBody.getWorldCenter(), true);
					}
				}
//				image.addAction(Actions.moveTo(mainBody.getPosition().x, mainBody.getPosition().y));
//				image.setPosition(mainBody.getPosition().x, mainBody.getPosition().y);
				System.out.println(String.format("mainBody position: (%s, %s)", mainBody.getPosition().x, mainBody.getPosition().y));
				System.out.println(String.format("v: %s", mainBody.getLinearVelocity()));
			}

			@Override
			public void onTouchLeft(int pointer) {
				if (Math.abs(mainBody.getLinearVelocity().x) < 32) {
					if (mainBody.getLinearVelocity().y != 0) {
//						mainBody.applyLinearImpulse(new Vector2(-4f, mainBody.getLinearVelocity().y), mainBody.getWorldCenter(), true);
					} else {
						mainBody.applyLinearImpulse(new Vector2(-4f, 0), mainBody.getWorldCenter(), true);
					}
				}

//				System.out.println(String.format("mainBody position: (%s, %s)", mainBody.getPosition().x, mainBody.getPosition().y));
//				image.setPosition(mainBody.getPosition().x, mainBody.getPosition().y);
			}

            @Override
            public void onJump(int pointer) {
				if (mainBody.getLinearVelocity().x != 0) {
					Vector2 v = mainBody.getLinearVelocity().add(0, 32f);
					mainBody.applyLinearImpulse(v, mainBody.getWorldCenter(), true);
//	                mainBody.applyLinearImpulse(new Vector2(0, 20000f), mainBody.getWorldCenter(), true);
				}
            }
        });
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1f, 1, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		batch.setProjectionMatrix(fixCamera.combined);
		world.step(1 / 10f, 8, 3);
		mapRender.setView(camera);
		mapRender.render();

		arror.render();

		box2dRender.render(world, camera.combined);

		stage.act();
		stage.draw();
		Actor mainAct = stage.getActors().get(0);
		mainAct.addAction(Actions.moveTo(mainBody.getPosition().x - mainAct.getWidth() / 2, mainBody.getPosition().y - mainAct.getHeight() / 2));
	}
	
	@Override
	public void dispose() {
//		batch.dispose();
		arror.dispose();
//		img.dispose();
		test.dispose();
		stage.dispose();
		map.dispose();
	}
}
