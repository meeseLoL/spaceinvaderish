/*
	Starter code for Project 2.
	A Space Invaders game with a row of aliens, a row of barriers, and 3 lives.

	TODO: Update this block comment and include any info I should know about your game.
*/
class SpaceInvaders
{
	// Constants
	final double ALIEN_START_SPEED = 0.2;
	final double BULLET_SPEED = 3.0;
	final double BOMB_SPEED = 0.2;
	final double ALIEN_Y_STEP = 2;
	final int MAX_LIVES = 3;
	final int MAX_ALIENS = 8;
	final int NUM_BARRIERS = 4;
	// TODO: Constants for MAX_ALIENS and NUM_BARRIERS

	// The game objects
	GameObj gun, scoreText;
	GameObj bullet = null;
	GameObj bomb = null;
	GameObj[] aliens; // array for aliens
	GameObj[] barriers; //array for barriers
	GameObj[] lives; // array for lives left icons
	// TODO: Arrays for aliens, barriers, and lives objects (for lives left display)
	// Use constants MAX_ALIENS, NUM_BARRIERS, MAX_LIVES

	// Array for the x-coordinate of each barrier
	double[] xBarrier = { 20, 40, 60, 80 };

	// TODO: Array xLives for the x-coordinate of the 3 lives left icons
	double[] xLives = { 1, 4, 7};


	// The game state
	double alienSpeed = ALIEN_START_SPEED;   // current alien speed
	int score = 0;                           // current score
	int livesLeft = MAX_LIVES;               // number of lives left

	

	public void start()
	{
		// arrays for aliens barriers and lives
		aliens = new GameObj[MAX_ALIENS];
		barriers = new GameObj[NUM_BARRIERS];
		lives = new GameObj[MAX_LIVES];

		// barriers using for loop and xBarrier array
		for (int i = 0; i < NUM_BARRIERS; i++)
		{
			barriers[i] = ct.rect(xBarrier[i], 80, 20, 5, "green");
		}

		//lives left display with for loop
		for (int i = 0; i < MAX_LIVES; i++)
		{
			lives[i] = ct.image("gun.png", xLives[i] * 3, 9, 5);
		}

		//create aliens using for loop to calc positions
		for (int i = 0; i< MAX_ALIENS; i++)
		{
			aliens[i] = ct.image("alien.png", 10 + i * 10, 10, 5);
		}


		// Create the gun and score text
		gun = makeGun(ct.getWidth() / 2, 70.5);
		scoreText = ct.text( "0", 90, 7, 10, "dark blue" );
	}

	public void update()
	{
		moveGun();
		moveAliens();
		doBullet();
		doBomb();
	}

	// Create and return a gun object at the specified (x, y) position
	GameObj makeGun( double x, double y )
	{
		// TODO: Create and return a gun object
		return ct.image("gun.png", x, y, 8);   // replace this to return a real object
	}

	// Allow the keyboard to move the gun
	void moveGun()
	{
		// TODO
		if (ct.keyPressed("left") && gun.x > 0)
		{
			gun.x -= 2;
		}
		else if (ct.keyPressed("right") && gun.x < ct.getWidth() - gun.getWidth())
		{
			gun.x += 2;
		}
		else if (ct.keyPressed("space") && bullet == null)
		{
			bullet = ct.image("bullet.png", gun.x, gun.y - 10, 4);
			bullet.setYSpeed(-BULLET_SPEED);
		}
	}

	// Move the aliens and check for hits
	void moveAliens()
	{
		// TODO: Use a for loop to loop over all aliens.
		// For each alien: 
		//    If this alien is not null:
		//         * Move the alien by alienSpeed (note: changing the alien's x value not using xSpeed)
		//         * If alien reaches the gun then call function playerLoses 
		//         * Check for bullet hitting the alien (check bullet is not null first)
		//         * If hit by bullet then use functions killAlien, addScore, deleteBullet, 
		//           and increase the alienSpeed

		for (int i = 0; i < MAX_ALIENS; i++)
		{
			if (aliens[i] != null)
			{
				aliens[i].x += alienSpeed;
				if (aliens[i].hit(gun))
				{
					loseLife();
					
				}
				if (bullet != null && bullet.hit(aliens[i]))
				{
					killAlien(i);
					addScore(1);
					deleteBullet();
					alienSpeed += 0.4;
					return;
				}
				if (aliens[i].y + aliens[i].getHeight() >= ct.getHeight())
				{
					playerLoses();
				}
			}
		}



		// TODO: Use function numAliensLeft to see how many are left,
		// and if there are none left then call function playerWins
		if (numAliensLeft() == 0)
		{
			playerWins();
			
		}

		// TODO: Check for aliens needing to change direction by calling
		// functions alienOffRight and alienOffLeft and then use a loop
		// to move all aliens down by ALIEN_Y_STEP. Reverse the alienSpeed.

		if (alienOffRight() || alienOffLeft())
		{
			alienSpeed = -alienSpeed;
			for (int i = 0; i < MAX_ALIENS; i++)
			{
				if (aliens[i] != null)
				{
					aliens[i].y += ALIEN_Y_STEP;
				}
			}
		}
	}

	// If there is not a bullet, allow space bar to fire one.
	// If there is one, delete it if it hit a barrier or went off-screen.
	void doBullet()
	{
		// Is there a bullet?
		if (bullet == null && ct.keyPressed("space"))
		{
			// TODO: No bullet yet, space bar fires one
			bullet = ct.image("bullet.png", gun.x, gun.y - 5, 1);
			bullet.setYSpeed(-BULLET_SPEED);
		}
		if (bullet != null && (bullet.y < 0 || objHitBarrier(bullet))) 
		{
			// Did the bullet go off-screen or hit a barrier?
			deleteBullet();
		}

	}

	// If there is not a bomb, make a random alien drop one.
	// If there is one, handle it hitting something or going off-screen.
	void doBomb()
	{
		// Is there a bomb?
		if (bomb == null)
		{
			// TODO: Choose a random alien to drop a bomb.
			// Hint: Choose a random array index and drop a bomb
			// from that alien only if the alien is not null.
			int randomIndex = ct.random(0, MAX_ALIENS - 1);
			GameObj randomAlien = aliens[randomIndex];
			if (randomAlien != null)
			{
				bomb = ct.image("bomb.png", randomAlien.x, randomAlien.y + randomAlien.getHeight(), 5);
				bomb.setYSpeed(BOMB_SPEED);
			}
		}
		else if (bomb.y > ct.getHeight() || bomb.hit(gun) || objHitBarrier(bomb))
		{
			// TODO: Did the bomb hit the gun or a barrier or go off-screen?
			// Use function objHitBarrier to test the barriers. 
			// Use functions deleteBomb and loseLife as appropriate.
			deleteBomb();
			
		}
	}

	// Add the specified number of points to the score and update the score display
	void addScore( int points )
	{
		// TODO
		score += points;
		scoreText.setText("" + score);
	}

	// Kill the alien at the specified array index i
	void killAlien( int i )
	{
		// TODO
		aliens[i].delete();
		aliens[i] = null;
	}

	// Delete the bullet
	void deleteBullet()
	{
		bullet.delete();
		bullet = null;
	}

	// TODO: Make function deleteBomb to delete the bomb
	void deleteBomb()
	{
		bomb.delete();
		bomb = null;
	}

	// Return true if the specified obj hit one of the barriers,
	// otherwise return false.
	boolean objHitBarrier( GameObj obj )
	{
		for (int i = 0; i < NUM_BARRIERS; i++)
		{
			if (obj.hit(barriers[i]))
			{
				return true;
			}
		}
		return false;
	}

	// Return true if any alien is off-screen to the right, 
	// otherwise return false.
	boolean alienOffRight()
	{
		for (int i = 0; i < MAX_ALIENS; i++)
		{
			if (aliens[i] != null && aliens[i].x + aliens[i].getWidth() > ct.getWidth())
			{
				return true;
			}
		}
		return false;
	}

	boolean alienOffLeft()
	{
		for (int i = 0; i < MAX_ALIENS; i++)
		{
			if (aliens[i] != null && aliens[i].x < 0)
			{
				return true;
			}
		}
		return false;
	}

	// Lose a life, update the lives display, and show Game Over
	// if there are no lives left.
	void loseLife()
	{
		// TODO: Update livesLeft and the lives array for the display.
		// Use function playerLoses if the player loses.

		livesLeft--;
		lives[livesLeft].delete();
		if (livesLeft == 0)
		{
			playerLoses();
		}
	}

	// TODO: Define function numAliensLeft, which takes no parameters
	// and returns the number of aliens left (how many are non-null).
	// Use a for loop to check each alien in the array whether it is null.
	int numAliensLeft()
	{
		int count = 0;
		for (int i = 0; i < MAX_ALIENS; i++)
		{
			if (aliens[i] != null)
			{
				count++;
			}
		}
		return count;
	}

	// Give Game Over alert and then end the game
	void playerLoses()
	{
		ct.showAlert( "Game Over!" );
		endGame();
	}

	// Tell the user that they won and then end the game
	void playerWins()
	{
		// TODO
		ct.showAlert("You Win!");
		endGame();
	}

	// Ask the user if they want to play again, and restart if so, else stop.
	void endGame()
	{
		if (ct.inputYesNo( "Would you like to play again?") )
			ct.restart();
		else
			ct.stop();
	}
}
