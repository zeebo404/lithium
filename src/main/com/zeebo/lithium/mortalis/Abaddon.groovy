package com.zeebo.lithium.mortalis
/**
 * Created with IntelliJ IDEA.
 * User: Eric Siebeneich
 * Date: 9/12/14
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */
class Abaddon {

	private static Thread deathTimer

	private static def managedObjects = []

	static {

		deathTimer = Thread.startDaemon {
			while(true) {
				def interrupted = false
				def obj = managedObjects[0]

				if (obj != null) {
					sleep(obj.@impendingDoom - System.currentTimeMillis(), {
						interrupted = true
					})
					if (!interrupted) {
						if (obj.@callback) {
							obj.@callback(obj.@delegate)
						}
						obj.@delegate = null
						managedObjects.remove(0)

						println 'Deleted object'
					}
					interrupted = false
				}
				else {
					sleep 10
				}
			}
		}
	}

	synchronized static def registerObject(def object, long lifespan, Closure callback = null) {
		long impendingDoom = System.currentTimeMillis() + lifespan

		def obj = new MortalObject(object, impendingDoom, callback)
		managedObjects.add(calculateInsertionIndex(impendingDoom), obj)

		deathTimer.interrupt()

		return obj
	}

	static def registerCollection(Collection collection) {
		DelegatingObject obj = new DelegatingObject(delegate: collection)

		obj.@preInvoke = { name, args ->
			obj.@delegate.removeAll { try { it.@delegate == null } catch (MissingFieldException mfe){} }
		}

		return obj
	}

	private static int calculateInsertionIndex(long impendingDoom, int left = 0, int right = managedObjects.size()) {
		if (left < right) {
			int index = (right + left) / 2
			if (managedObjects[index].@impendingDoom < impendingDoom) {
				return calculateInsertionIndex(impendingDoom, index + 1, right)
			}
			else {
				return calculateInsertionIndex(impendingDoom, left, index - 1)
			}
		}
		else {
			return right // its actually smaller potentially
		}
	}

	public static void main(String[] args) {

		def list = []
		list = Abaddon.registerCollection(list)

		list << Abaddon.registerObject('1', 1000) { println it }
		list << Abaddon.registerObject('2', 3400) { println it }
		list << Abaddon.registerObject('3', 1400) { println it }

		list.add(0, Abaddon.registerObject('4', 3000, {println it}))

		list << 'test'

		while (list.size() != 1) {
			println( list.collect { return it } )
			sleep 100
		}
	}
}