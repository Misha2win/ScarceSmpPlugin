package me.misha2win.scracesmpplugin.util;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleMaker {
	
	public static void createDome(Particle p, Location center, int radius, int detail) {
		for (double i = 0; i < Math.PI; i += Math.PI / detail) {
			double rad = radius * Math.sin(i);
			double height = radius * Math.cos(i);
			
			for (double j = 0; j < 2 * Math.PI; j += Math.PI / detail) {
				double x = rad * Math.cos(j);
				double z = rad * Math.sin(j);
				
				center.getWorld().spawnParticle(p, center.getX() + x, center.getY() + height, center.getZ() + z, 1);
			}
		}
	}
	
	public static void createCylinder(Particle particle, Location location, double radius, double height) {
		double px = location.getX();
		double pz = location.getZ();
		
		for (double py = location.getY(); py < location.getY() + height; py += .01) {
			double degree = 2 * Math.PI * (location.getY() - py);
			
			double pxOffset = radius * Math.sin(degree);
			double pzOffset = radius * Math.cos(degree);
			
			location.getWorld().spawnParticle(particle, px + pxOffset, py, pz + pzOffset, 1);
		}
	}

}
