package io.github.plenglin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * The worst pong experience you'll ever have
 * <p> The ball accelerates with each bounce.
 * @author Plenglin
 *
 */
public class Pong extends ApplicationAdapter implements ContactListener {
	
	/**
	 * Window
	 */
	public static final int WIDTH = 800, HEIGHT = 600;
	
	public static final float BALL_BOUNCE = 1.1f;
	
	/**
	 * m/s
	 */
	public static final float BALL_INITIAL = 3f, PLAYER_SPEED = 3f;
	
	/**
	 * meters
	 */
	public static final float ARENA_WIDTH = 10, ARENA_HEIGHT = 7,
			PLAYER_WIDTH = 0.25f, PLAYER_HEIGHT = 1;	
	
	World world;
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;
	Box2DDebugRenderer debugRenderer;
		
	/**
	 * players
	 */
	Body p1, p2;
	/**
	 * the ball
	 */
	Body ball;
	/**
	 * sensors
	 */
	Body s1, s2;

	private boolean resetBall;
	
	@Override
	public void create() {
		
		world = new World(new Vector2(0, 0), true);
		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		shape = new ShapeRenderer();
		//font = new BitmapFont(Gdx.files.classpath("assets/ubuntu-100.fnt"), true);
		debugRenderer = new Box2DDebugRenderer();
		
		cam.setToOrtho(true, 20, 14);
		cam.position.set(3, 2, 0);
				
		// Make the ball
		BodyDef ballb = new BodyDef();
		ballb.type = BodyType.DynamicBody;
		ballb.position.set(ARENA_WIDTH / 2, ARENA_HEIGHT / 2);
		
		CircleShape ballshape = new CircleShape();
		ballshape.setRadius(0.5f);
		
		FixtureDef ballf = new FixtureDef();
		ballf.restitution = BALL_BOUNCE;
		ballf.friction = 0f;
		ballf.density = 1f;
		ballf.shape = ballshape;
		
		// Make the players
		BodyDef player1def = new BodyDef();
		player1def.type = BodyType.KinematicBody;
		player1def.position.set(0, ARENA_HEIGHT / 2);
		
		BodyDef player2def = new BodyDef();
		player2def.type = BodyType.KinematicBody;
		player2def.position.set(ARENA_WIDTH, ARENA_HEIGHT / 2);
		
		PolygonShape playershape = new PolygonShape();
		playershape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2);
		
		FixtureDef playerf = new FixtureDef();
		playerf.restitution = 1f;
		playerf.friction = 0f;
		playerf.shape = playershape;
		
		// Create the walls
		BodyDef wallU = new BodyDef();
		wallU.type = BodyType.StaticBody;
		wallU.position.set(ARENA_WIDTH / 2, 0);
		
		BodyDef wallL = new BodyDef();
		wallL.type = BodyType.StaticBody;
		wallL.position.set(ARENA_WIDTH / 2, ARENA_HEIGHT);
		
		PolygonShape wallshape = new PolygonShape();
		wallshape.setAsBox(ARENA_WIDTH / 2, 0.5f);
		
		FixtureDef wallf = new FixtureDef();
		wallf.restitution = 1f;
		wallf.friction = 0f;
		wallf.shape = wallshape;
		
		// Create win condition sensors
		BodyDef sensor1 = new BodyDef();
		sensor1.type = BodyType.StaticBody;
		sensor1.position.set(-1, ARENA_HEIGHT / 2);

		BodyDef sensor2 = new BodyDef();
		sensor2.type = BodyType.StaticBody;
		sensor2.position.set(ARENA_WIDTH+1, ARENA_HEIGHT / 2);
		
		PolygonShape sensorshape = new PolygonShape();
		sensorshape.setAsBox(0f, ARENA_HEIGHT);
		
		FixtureDef sensorf = new FixtureDef();
		sensorf.restitution = 1;
		sensorf.isSensor = true;
		sensorf.shape = sensorshape;
				
		// Attach everything
		ball = world.createBody(ballb);
		ball.createFixture(ballf);
		
		p1 = world.createBody(player1def);
		p2 = world.createBody(player2def);
		
		p1.createFixture(playerf);
		p2.createFixture(playerf);
		
		world.createBody(wallU).createFixture(wallf);
		world.createBody(wallL).createFixture(wallf);
		
		s1 = world.createBody(sensor1);
		s2 = world.createBody(sensor2);
		
		s1.createFixture(sensorf);
		s2.createFixture(sensorf);
		
		world.setContactListener(this);
		
		// Dispose everything
		ballshape.dispose();
		playershape.dispose();
		wallshape.dispose();
		sensorshape.dispose();
		
		debugRenderer.setDrawVelocities(true);
		
		resetBall();
				
	}

	@Override
	public void render() {
				
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (resetBall) {
			resetBall();
			resetBall = false;
		}
		
		Vector2 p1pos = p1.getPosition();
		Vector2 p2pos = p2.getPosition();
		
		if (Gdx.input.isKeyPressed(Keys.W) && p1pos.y > 0) {
			p1.setTransform(p1pos.x, p1pos.y - (PLAYER_SPEED / 60f), 0);
		} 
		if (Gdx.input.isKeyPressed(Keys.S) && p1pos.y < ARENA_HEIGHT) {
			p1.setTransform(p1pos.x, p1pos.y + (PLAYER_SPEED / 60f), 0);
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP) && p2pos.y > 0) {
			p2.setTransform(p2pos.x, p2pos.y - (PLAYER_SPEED / 60f), 0);
		} 
		if (Gdx.input.isKeyPressed(Keys.DOWN) && p2pos.y < ARENA_HEIGHT) {
			p2.setTransform(p2pos.x, p2pos.y + (PLAYER_SPEED / 60f), 0);
		}
		
		Vector2 ballSpeed = ball.getLinearVelocity();
		if (0.1 > ballSpeed.x/ballSpeed.y && ballSpeed.x/ballSpeed.y > -0.1) {
			ball.setLinearVelocity(ballSpeed.y, ballSpeed.y);
		}
		if (ballSpeed.y == 0) {
			ball.setLinearVelocity(ballSpeed.x, ballSpeed.x);
		}
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		batch.begin();
		debugRenderer.render(world, cam.combined);
		batch.end();
		
		world.step(1/60f, 6, 2);
	
	}
	
	@Override
	public void dispose() {
		world.dispose();
		batch.dispose();
		shape.dispose();
		//font.dispose();
		debugRenderer.dispose();
	}

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getBody() == s1 || contact.getFixtureB().getBody() == s1 ||
			contact.getFixtureA().getBody() == s2 || contact.getFixtureB().getBody() == s2) {
			resetBall = true;
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
	void resetBall() {
		ball.setTransform(ARENA_WIDTH / 2, ARENA_HEIGHT / 2, 0);
		double angle = Math.PI/6 * (Math.random() + 1d) + (int) (4*Math.random()) * Math.PI/2;
		ball.setLinearVelocity((float) (BALL_INITIAL*Math.cos(angle)), (float) (BALL_INITIAL*Math.sin(angle)));
	}

}
