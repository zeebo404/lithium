package com.zeebo.lithium.mortalis

/**
 * Created with IntelliJ IDEA.
 * User: Eric Siebeneich
 * Date: 9/12/14
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */
class MortalObject implements GroovyInterceptable {

	private long impendingDoom

	private def delegate = null

	private Closure callback

	MortalObject(def delegate, long doom, Closure cb = null) {
		this.@delegate = delegate
		this.@impendingDoom = doom
		this.@callback = cb
	}

	def invokeMethod(String name, args) {
		if (args.size() > 0) {
			this.@delegate?."${name}"(args)
		}
		else {
			this.@delegate?."${name}"()
		}
	}
	def getProperty(String name) {
		return this.@delegate?."${name}"
	}
	void setProperty(String name, value) {
		this.@delegate."${name}" = value
	}

	String toString() {
		return this?.@delegate?.toString()
	}
}
